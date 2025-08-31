package com.paraskcd.influentiallauncher.data.managers

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import com.microsoft.fluent.mobile.icons.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WifiStatusManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val _level = MutableStateFlow(0)
    val level: StateFlow<Int> = _level.asStateFlow()

    private val listener = object : ConnectivityManager.NetworkCallback() {
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            val wifiInfo = networkCapabilities.transportInfo as? WifiInfo
            val rssi = wifiInfo?.rssi ?: Int.MIN_VALUE
            val calc = if (rssi == Int.MIN_VALUE) 0 else updateSignalStrengthLevels(rssi)
            _level.value = calc.coerceIn(0, 4)
        }
    }

    init {
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        connectivityManager.registerNetworkCallback(request, listener)
    }

    fun getWifiDrawableForLevel(l: Int): Int {
        return when (l.coerceIn(0, 4)) {
            4 -> R.drawable.ic_fluent_wifi_1_24_regular
            3 -> R.drawable.ic_fluent_wifi_2_24_regular
            2 -> R.drawable.ic_fluent_wifi_3_24_regular
            1 -> R.drawable.ic_fluent_wifi_4_24_regular
            else -> R.drawable.ic_fluent_wifi_4_24_regular
        }
    }

    private fun updateSignalStrengthLevels(signalStrength: Int): Int {
        return when {
            signalStrength >= -60 -> 4
            signalStrength >= -70 -> 3
            signalStrength >= -80 -> 2
            signalStrength >= -90 -> 1
            else -> 0
        }
    }
}