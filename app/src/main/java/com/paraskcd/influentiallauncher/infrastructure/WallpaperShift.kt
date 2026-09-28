// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.infrastructure

import android.app.WallpaperManager
import android.os.IBinder
import android.util.Log
import android.view.Window
import org.lsposed.hiddenapibypass.HiddenApiBypass
import java.lang.reflect.Method

object WallpaperShift {
    private const val LogTag = "WallpaperShift"
    private const val ShiftMethod = "setDisplayOffset"
    private const val WallpaperManagerSignature = "Landroid/app/WallpaperManager;"
    private var lastX = Int.MIN_VALUE
    private var lastY = Int.MIN_VALUE
    private val method: Method? by lazy {
        runCatching {
            HiddenApiBypass.addHiddenApiExemptions(WallpaperManagerSignature)
            WallpaperManager::class.java.getMethod(ShiftMethod, IBinder::class.java, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
        }
            .onFailure { Log.w(LogTag, "wallpaper shift unavailable", it) }
            .getOrNull()
    }

    fun set(window: Window, xPx: Int, yPx: Int) {
        if (xPx == lastX && yPx == lastY) return
        val token = window.decorView.windowToken ?: return
        val shift = method ?: return
        runCatching { shift.invoke(WallpaperManager.getInstance(window.context), token, xPx, yPx) }
            .onSuccess {
                lastX = xPx
                lastY = yPx
            }
            .onFailure { Log.w(LogTag, "wallpaper shift failed", it) }
    }
}
