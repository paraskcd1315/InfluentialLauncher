package com.paraskcd.influentiallauncher.devicestatus.infrastructure

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
import android.telephony.SignalStrength
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import com.paraskcd.influentiallauncher.devicestatus.domain.model.BatteryStatus
import com.paraskcd.influentiallauncher.devicestatus.domain.model.CellularStatus
import com.paraskcd.influentiallauncher.devicestatus.domain.model.SignalLevel
import com.paraskcd.influentiallauncher.devicestatus.domain.model.WifiStatus
import com.paraskcd.influentiallauncher.devicestatus.domain.model.signalLevelOf
import com.paraskcd.influentiallauncher.devicestatus.domain.ports.DeviceStatusSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidDeviceStatusSource @Inject constructor(
    @ApplicationContext private val context: Context
) : DeviceStatusSource {

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
        val connectivity = context.getSystemService(ConnectivityManager::class.java)
        val wifiManager = context.getSystemService(WifiManager::class.java)
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                val bars = wifiManager.calculateSignalLevel(capabilities.signalStrength)
                val scaled = bars * SignalLevel.entries.lastIndex / wifiManager.maxSignalLevel.coerceAtLeast(1)
                trySend(WifiStatus(connected = true, level = signalLevelOf(scaled)))
            }

            override fun onLost(network: Network) {
                trySend(Disconnected)
            }
        }
        trySend(Disconnected)
        val request = NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build()
        connectivity.registerNetworkCallback(request, callback)
        awaitClose { connectivity.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()

    override val cellular: Flow<CellularStatus> =
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_RADIO_ACCESS)) {
            flowOf(NoCellular)
        } else {
            callbackFlow {
                val telephony = context.getSystemService(TelephonyManager::class.java)
                val callback = object : TelephonyCallback(), TelephonyCallback.SignalStrengthsListener {
                    override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                        val ready = telephony.simState == TelephonyManager.SIM_STATE_READY
                        trySend(CellularStatus(available = ready, level = signalLevelOf(signalStrength.level)))
                    }
                }
                trySend(CellularStatus(available = telephony.simState == TelephonyManager.SIM_STATE_READY, level = SignalLevel.None))
                telephony.registerTelephonyCallback(ContextCompat.getMainExecutor(context), callback)
                awaitClose { telephony.unregisterTelephonyCallback(callback) }
            }.distinctUntilChanged()
        }

    private fun batteryOf(intent: Intent): BatteryStatus {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, PercentScale).coerceAtLeast(1)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
        val charging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
        return BatteryStatus(percent = level * PercentScale / scale, charging = charging)
    }

    private companion object {
        const val PercentScale = 100
        val Disconnected = WifiStatus(connected = false, level = SignalLevel.None)
        val NoCellular = CellularStatus(available = false, level = SignalLevel.None)
    }
}
