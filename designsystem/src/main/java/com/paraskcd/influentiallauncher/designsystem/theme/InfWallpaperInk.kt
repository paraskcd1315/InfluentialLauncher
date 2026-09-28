// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/** The text colour and shadow colour for content drawn straight on the wallpaper. */
@Immutable
data class WallpaperInk(val content: Color, val shadow: Color) {
    companion object {
        val Light = WallpaperInk(content = InfContrast.LightContent, shadow = Color.Black)
        val Dark = WallpaperInk(content = InfContrast.DarkContent, shadow = Color.White)

        fun forDarkText(darkText: Boolean): WallpaperInk = if (darkText) Dark else Light
    }
}

val LocalWallpaperInk = compositionLocalOf { WallpaperInk.Light }
