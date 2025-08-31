package com.paraskcd.influentiallauncher.data.managers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.PhoneStateListener
import android.telephony.ServiceState
import android.telephony.SignalStrength
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.microsoft.fluent.mobile.icons.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CellularStatusManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

    private val _level = MutableStateFlow(0)
    val level: StateFlow<Int> = _level.asStateFlow()

    private val _networkType = MutableStateFlow("")
    val networkType: StateFlow<String> = _networkType.asStateFlow()

    private val listener = object : PhoneStateListener() {
        override fun onSignalStrengthsChanged(signalStrength: SignalStrength?) {
            super.onSignalStrengthsChanged(signalStrength)
            val lvl = try {
                signalStrength?.level ?: 0
            } catch (e: Throwable) {
                0
            }
            _level.value = lvl.coerceIn(0, 4)

            val phoneStateGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED

            if (phoneStateGranted) {
                _networkType.value = resolveNetworkType()
            }
        }

        @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
        override fun onDataConnectionStateChanged(state: Int, networkType: Int) {
            super.onDataConnectionStateChanged(state, networkType)

            val phoneStateGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED

            if (phoneStateGranted) {
                _networkType.value = resolveNetworkType()
            }
        }

        override fun onServiceStateChanged(serviceState: ServiceState?) {
            super.onServiceStateChanged(serviceState)
            if (serviceState?.state == ServiceState.STATE_POWER_OFF) {
                _level.value = 0
                _networkType.value = ""
            }
        }
    }

    init {
        telephonyManager?.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
    }

    fun getCellularDrawableForLevel(l: Int): Int {
        return when (l.coerceIn(0, 4)) {
            4 -> R.drawable.ic_fluent_cellular_data_1_24_regular
            3 -> R.drawable.ic_fluent_cellular_data_2_24_regular
            2 -> R.drawable.ic_fluent_cellular_data_3_24_regular
            1 -> R.drawable.ic_fluent_cellular_data_4_24_regular
            else -> R.drawable.ic_fluent_cellular_data_5_24_regular
        }
    }

    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    private fun resolveNetworkType(): String {
        val type = telephonyManager?.dataNetworkType ?: TelephonyManager.NETWORK_TYPE_UNKNOWN
        return when (type) {
            TelephonyManager.NETWORK_TYPE_GPRS,
            TelephonyManager.NETWORK_TYPE_EDGE,
            TelephonyManager.NETWORK_TYPE_CDMA,
            TelephonyManager.NETWORK_TYPE_1xRTT,
            TelephonyManager.NETWORK_TYPE_IDEN -> "2G"
            TelephonyManager.NETWORK_TYPE_UMTS,
            TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSUPA,
            TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_EVDO_B,
            TelephonyManager.NETWORK_TYPE_EHRPD,
            TelephonyManager.NETWORK_TYPE_HSPAP -> "3G"
            TelephonyManager.NETWORK_TYPE_LTE, -> "4G"
            TelephonyManager.NETWORK_TYPE_NR -> "5G"
            else -> ""
        }
    }
}