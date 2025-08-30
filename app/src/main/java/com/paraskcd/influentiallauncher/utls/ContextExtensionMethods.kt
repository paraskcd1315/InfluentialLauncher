package com.paraskcd.influentiallauncher.utls

import android.content.Context
import android.content.Intent
import android.net.Uri
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