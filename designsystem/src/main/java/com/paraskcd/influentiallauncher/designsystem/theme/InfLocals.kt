package com.paraskcd.influentiallauncher.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf

private val DefaultRamp = brandRampOf(InfBrandSeed)

val LocalInfColors = staticCompositionLocalOf { infDarkColors(DefaultRamp) }
val LocalInfBrandRamp = staticCompositionLocalOf { DefaultRamp }
