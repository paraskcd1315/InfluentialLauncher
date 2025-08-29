package com.paraskcd.influentiallauncher.utls

import android.content.Context
import android.view.WindowManager

fun Context.isWindowBlurSupported(): Boolean {
    val wm = getSystemService(WindowManager::class.java) ?: return false
    return wm.isCrossWindowBlurEnabled
}