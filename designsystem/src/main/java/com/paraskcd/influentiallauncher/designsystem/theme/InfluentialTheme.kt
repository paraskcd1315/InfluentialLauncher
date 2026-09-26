package com.paraskcd.influentiallauncher.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle

@Composable
fun InfluentialTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val wallpaper = remember(context, darkTheme) {
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    val ramp = remember(wallpaper) { brandRampOf(wallpaper.primary) }
    val colors = remember(ramp, darkTheme, wallpaper) {
        (if (darkTheme) infDarkColors(ramp) else infLightColors(ramp)).withWallpaper(wallpaper)
    }
    CompositionLocalProvider(
        LocalInfColors provides colors,
        LocalInfBrandRamp provides ramp
    ) {
        MaterialTheme(colorScheme = wallpaper, typography = InfTypography) {
            CompositionLocalProvider(LocalTextStyle provides TextStyle(fontFamily = Quicksand, color = colors.textPrimary)) {
                content()
            }
        }
    }
}
