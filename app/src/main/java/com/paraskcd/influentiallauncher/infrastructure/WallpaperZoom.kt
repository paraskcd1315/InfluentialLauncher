// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.infrastructure

import android.app.WallpaperManager
import android.os.IBinder
import android.util.Log
import android.view.Window
import org.lsposed.hiddenapibypass.HiddenApiBypass
import java.lang.reflect.Method

object WallpaperZoom {
    private const val LogTag = "WallpaperZoom"
    private const val ZoomMethod = "setWallpaperZoomOut"
    private const val WallpaperManagerSignature = "Landroid/app/WallpaperManager;"
    private var last = -1f
    private val method: Method? by lazy {
        runCatching {
            HiddenApiBypass.addHiddenApiExemptions(WallpaperManagerSignature)
            WallpaperManager::class.java.getMethod(ZoomMethod, IBinder::class.java, Float::class.javaPrimitiveType)
        }
            .onFailure { Log.w(LogTag, "wallpaper zoom unavailable", it) }
            .getOrNull()
    }

    fun set(window: Window, zoom: Float) {
        val value = zoom.coerceIn(0f, 1f)
        if (value == last) return
        val token = window.decorView.windowToken ?: return
        val zoomOut = method ?: return
        runCatching { zoomOut.invoke(WallpaperManager.getInstance(window.context), token, value) }
            .onSuccess { last = value }
            .onFailure { Log.w(LogTag, "wallpaper zoom failed", it) }
    }
}
