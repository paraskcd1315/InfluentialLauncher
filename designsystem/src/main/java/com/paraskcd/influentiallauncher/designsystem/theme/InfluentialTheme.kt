// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle

@Composable
fun InfluentialTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val seed = remember(context, darkTheme) {
        if (darkTheme) dynamicDarkColorScheme(context).primary else dynamicLightColorScheme(context).primary
    }
    val ramp = remember(seed) { brandRampOf(seed) }
    val colors = remember(ramp, darkTheme) { if (darkTheme) infDarkColors(ramp) else infLightColors(ramp) }
    val scheme = remember(colors) {
        val base = if (colors.isLight) lightColorScheme() else darkColorScheme()
        base.copy(
            primary = colors.brand,
            onPrimary = Color.White,
            surfaceTint = colors.brand,
            background = colors.bgBase,
            surface = colors.surface,
            surfaceBright = colors.surfaceContainerHigh,
            surfaceContainer = colors.surfaceContainer,
            surfaceContainerHigh = colors.surfaceContainerHigh,
            onSurface = colors.textPrimary,
            onBackground = colors.textPrimary,
            onSurfaceVariant = colors.textSecondary,
            outline = colors.border,
            error = colors.danger
        )
    }
    CompositionLocalProvider(
        LocalInfColors provides colors,
        LocalInfBrandRamp provides ramp
    ) {
        MaterialTheme(colorScheme = scheme, typography = InfTypography) {
            CompositionLocalProvider(LocalTextStyle provides TextStyle(fontFamily = Quicksand, color = colors.textPrimary)) {
                content()
            }
        }
    }
}
