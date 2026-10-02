// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.devicestatus.infrastructure

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.telephony.CarrierConfigManager
import android.telephony.SignalStrength
import android.telephony.SubscriptionManager
import android.telephony.TelephonyCallback
import android.telephony.TelephonyDisplayInfo
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.paraskcd.influentiallauncher.devicestatus.domain.model.BatteryStatus
import com.paraskcd.influentiallauncher.devicestatus.domain.model.CellularStatus
import com.paraskcd.influentiallauncher.devicestatus.domain.model.MobileDataType
import com.paraskcd.influentiallauncher.devicestatus.domain.model.SignalLevel
import com.paraskcd.influentiallauncher.devicestatus.domain.model.WifiStatus
import com.paraskcd.influentiallauncher.devicestatus.domain.model.signalLevelOf
import com.paraskcd.influentiallauncher.devicestatus.domain.ports.DeviceStatusSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class AndroidDeviceStatusSource @Inject constructor(
    @ApplicationContext private val context: Context
) : DeviceStatusSource {

    private val connectivity = context.getSystemService(ConnectivityManager::class.java)
    private val simSetup = MutableStateFlow(readSimSetup())

    override val battery: Flow<BatteryStatus> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                trySend(batteryOf(intent))
            }
        }
        val sticky = ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        if (sticky != null) trySend(batteryOf(sticky))
        awaitClose { context.unregisterReceiver(receiver) }
    }.distinctUntilChanged()

    override val wifi: Flow<WifiStatus> = callbackFlow {
        val wifiManager = context.getSystemService(WifiManager::class.java)
        val levels = mutableMapOf<Network, SignalLevel>()
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                val rssi = capabilities.signalStrength
                levels[network] = if (rssi == NetworkCapabilities.SIGNAL_STRENGTH_UNSPECIFIED) {
                    levels[network] ?: SignalLevel.None
                } else {
                    val bars = wifiManager.calculateSignalLevel(rssi)
                    signalLevelOf(bars * SignalLevel.entries.lastIndex / wifiManager.maxSignalLevel.coerceAtLeast(1))
                }
                trySend(wifiOf(levels.values))
            }

            override fun onLost(network: Network) {
                levels.remove(network)
                trySend(wifiOf(levels.values))
            }
        }
        trySend(Disconnected)
        connectivity.registerNetworkCallback(WifiRequest, callback)
        awaitClose { connectivity.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()

    override val cellular: Flow<CellularStatus> = simSetup.flatMapLatest { setup ->
        if (setup.subscriptions.isEmpty()) {
            flowOf(NoCellular)
        } else {
            combine(
                combine(setup.subscriptions.map(::simSignal)) { signals: Array<SignalLevel?> -> signals.filterNotNull() },
                displayedDataType(setup.dataSubscription),
                connected(MobileDataRequest)
            ) { sims, type, onMobileData ->
                CellularStatus(sims = sims, dataType = type.takeIf { onMobileData })
            }
        }
    }.distinctUntilChanged()

    override val vpn: Flow<Boolean> = connected(VpnRequest)

    override fun refresh() {
        simSetup.value = readSimSetup()
    }

    private fun simSignal(subscription: Int): Flow<SignalLevel?> = callbackFlow {
        val telephony = telephonyFor(subscription)
        val callback = object : TelephonyCallback(), TelephonyCallback.SignalStrengthsListener {
            override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                trySend(signalLevelOf(signalStrength.level).takeIf { isSimReady(telephony) })
            }
        }
        trySend(SignalLevel.None.takeIf { isSimReady(telephony) })
        val registered = register(telephony, callback)
        awaitClose { if (registered) telephony.unregisterTelephonyCallback(callback) }
    }

    private fun displayedDataType(subscription: Int): Flow<MobileDataType?> = callbackFlow {
        trySend(null)
        val telephony = telephonyFor(subscription)
        val naming = withContext(Dispatchers.IO) { carrierDataNaming(subscription) }
        val callback = object : TelephonyCallback(), TelephonyCallback.DisplayInfoListener {
            override fun onDisplayInfoChanged(telephonyDisplayInfo: TelephonyDisplayInfo) {
                trySend(
                    mobileDataTypeOf(
                        networkType = telephonyDisplayInfo.networkType,
                        overrideType = telephonyDisplayInfo.overrideNetworkType,
                        naming = naming
                    )
                )
            }
        }
        val registered = register(telephony, callback)
        awaitClose { if (registered) telephony.unregisterTelephonyCallback(callback) }
    }

    private fun connected(request: NetworkRequest): Flow<Boolean> = callbackFlow {
        val networks = mutableSetOf<Network>()
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                networks.add(network)
                trySend(true)
            }

            override fun onLost(network: Network) {
                networks.remove(network)
                trySend(networks.isNotEmpty())
            }
        }
        trySend(false)
        connectivity.registerNetworkCallback(request, callback)
        awaitClose { connectivity.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()

    private fun readSimSetup(): SimSetup {
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_RADIO_ACCESS)) return NoSims
        return runCatching {
            val telephony = context.getSystemService(TelephonyManager::class.java)
            val active = SubscriptionManager.getActiveDataSubscriptionId()
            val data = if (active != SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
                active
            } else {
                SubscriptionManager.getDefaultDataSubscriptionId()
            }
            val subscriptions = (0 until telephony.activeModemCount)
                .map { slot -> SubscriptionManager.getSubscriptionId(slot) }
                .filter { it != SubscriptionManager.INVALID_SUBSCRIPTION_ID }
                .sortedByDescending { it == data }
            SimSetup(
                subscriptions = subscriptions,
                dataSubscription = data,
                phoneStateGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) ==
                    PackageManager.PERMISSION_GRANTED
            )
        }.onFailure { Log.w(LogTag, "sim setup failed", it) }.getOrDefault(NoSims)
    }

    private fun telephonyFor(subscription: Int): TelephonyManager {
        val telephony = context.getSystemService(TelephonyManager::class.java)
        return if (subscription == SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
            telephony
        } else {
            telephony.createForSubscriptionId(subscription)
        }
    }

    private fun isSimReady(telephony: TelephonyManager): Boolean = telephony.simState == TelephonyManager.SIM_STATE_READY

    private fun register(telephony: TelephonyManager, callback: TelephonyCallback): Boolean =
        runCatching { telephony.registerTelephonyCallback(ContextCompat.getMainExecutor(context), callback) }
            .onFailure { Log.w(LogTag, "telephony callback refused", it) }
            .isSuccess

    private fun carrierDataNaming(subscription: Int): CarrierDataNaming = runCatching {
        val config = context.getSystemService(CarrierConfigManager::class.java).getConfigForSubId(
            subscription,
            CarrierConfigManager.KEY_SHOW_4G_FOR_LTE_DATA_ICON_BOOL,
            Show4gLteForLteKey,
            CarrierConfigManager.KEY_SHOW_4G_FOR_3G_DATA_ICON_BOOL,
            CarrierConfigManager.KEY_HIDE_LTE_PLUS_DATA_ICON_BOOL
        )
        CarrierDataNaming(
            show4gForLte = config.getBoolean(CarrierConfigManager.KEY_SHOW_4G_FOR_LTE_DATA_ICON_BOOL),
            show4gLteForLte = config.getBoolean(Show4gLteForLteKey),
            show4gFor3g = config.getBoolean(CarrierConfigManager.KEY_SHOW_4G_FOR_3G_DATA_ICON_BOOL),
            hideLtePlus = config.getBoolean(CarrierConfigManager.KEY_HIDE_LTE_PLUS_DATA_ICON_BOOL)
        )
    }.onFailure { Log.w(LogTag, "carrier naming unavailable", it) }.getOrDefault(CarrierDataNaming())

    private fun wifiOf(levels: Collection<SignalLevel>): WifiStatus =
        levels.maxOrNull()?.let { WifiStatus(connected = true, level = it) } ?: Disconnected

    private fun batteryOf(intent: Intent): BatteryStatus {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, PercentScale).coerceAtLeast(1)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
        val charging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
        return BatteryStatus(percent = level * PercentScale / scale, charging = charging)
    }

    private companion object {
        const val LogTag = "DeviceStatus"
        const val PercentScale = 100
        const val Show4gLteForLteKey = "show_4glte_for_lte_data_icon_bool"
        val Disconnected = WifiStatus(connected = false, level = SignalLevel.None)
        val NoCellular = CellularStatus(sims = emptyList(), dataType = null)
        val NoSims = SimSetup(
            subscriptions = emptyList(),
            dataSubscription = SubscriptionManager.INVALID_SUBSCRIPTION_ID,
            phoneStateGranted = false
        )
        val WifiRequest: NetworkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        val MobileDataRequest: NetworkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        val VpnRequest: NetworkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_VPN)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN)
            .build()
    }
}
