package com.paraskcd.influentiallauncher.utls

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.view.WindowManager
import androidx.core.net.toUri

fun Context.isWindowBlurSupported(): Boolean {
    val wm = getSystemService(WindowManager::class.java) ?: return false
    return wm.isCrossWindowBlurEnabled
}

fun Context.openAccessibilityServiceSettings() {
    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}

fun Context.shouldAttemptWeather(): Boolean {
    val pm = getSystemService(PowerManager::class.java)
    if (pm.isPowerSaveMode) return false

    val cm = getSystemService(ConnectivityManager::class.java)
    val net = cm.activeNetwork ?: return false
    val caps = cm.getNetworkCapabilities(net) ?: return false
    val ok = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
            (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))

    return ok
}