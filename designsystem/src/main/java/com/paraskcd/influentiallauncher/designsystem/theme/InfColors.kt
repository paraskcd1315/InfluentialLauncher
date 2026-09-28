// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class InfColors(
    val isLight: Boolean,
    val brand: Color,
    val brandText: Color,
    val brandTint: Color,
    val onBrand: Color,
    val bgBase: Color,
    val surface: Color,
    val surfaceContainer: Color,
    val surfaceContainerHigh: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val glassBg: Color,
    val glassStrongBg: Color,
    val glassBorder: Color,
    val glassSpecular: Color,
    val border: Color,
    val hairline: Color,
    val scrim: Color,
    val success: Color,
    val warning: Color,
    val danger: Color,
    val dangerText: Color
)
