// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.presentation

import android.graphics.RectF
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import com.paraskcd.influentiallauncher.designsystem.theme.WallpaperInk
import com.paraskcd.influentiallauncher.infrastructure.WallpaperRegionColors
import kotlin.math.roundToInt

/** The ink for content drawn over [bounds], a window-space rectangle of a [screen]-sized window. */
@Composable
fun rememberWallpaperInk(bounds: Rect?, screen: IntSize): WallpaperInk {
    val context = LocalContext.current
    val colors = remember(context) { WallpaperRegionColors(context.applicationContext) }
    val region = remember(bounds, screen) { bounds?.let { fractionOf(it, screen) } }
    var ink by remember { mutableStateOf(WallpaperInk.Light) }
    DisposableEffect(region) {
        val subscription = region?.let { colors.observe(it) { dark -> ink = WallpaperInk.forDarkText(dark) } }
        onDispose { subscription?.close() }
    }
    return ink
}

private fun fractionOf(bounds: Rect, screen: IntSize): RectF? {
    if (screen.width <= 0 || screen.height <= 0 || bounds.isEmpty) return null
    fun snap(value: Float, total: Int) = ((value / total).coerceIn(0f, 1f) * RegionSteps).roundToInt() / RegionSteps
    return RectF(snap(bounds.left, screen.width), snap(bounds.top, screen.height), snap(bounds.right, screen.width), snap(bounds.bottom, screen.height))
}

private const val RegionSteps = 50f
