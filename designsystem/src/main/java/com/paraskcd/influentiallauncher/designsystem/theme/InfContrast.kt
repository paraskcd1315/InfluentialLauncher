// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/** Picks the text colour that reads best on a background, by WCAG contrast ratio. */
object InfContrast {
    val LightContent = Color.White
    val DarkContent = Color(0xFF0B1210)

    fun contentOn(background: Color): Color {
        val bg = background.luminance()
        return if (ratio(LightContent.luminance(), bg) >= ratio(DarkContent.luminance(), bg)) LightContent else DarkContent
    }

    fun ratio(first: Float, second: Float): Float {
        val lighter = maxOf(first, second)
        val darker = minOf(first, second)
        return (lighter + Offset) / (darker + Offset)
    }

    private const val Offset = 0.05f
}
