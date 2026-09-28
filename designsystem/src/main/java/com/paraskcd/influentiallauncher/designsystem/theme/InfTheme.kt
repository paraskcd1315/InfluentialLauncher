// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

object InfTheme {
    val colors: InfColors
        @Composable @ReadOnlyComposable get() = LocalInfColors.current

    val ramp: BrandRamp
        @Composable @ReadOnlyComposable get() = LocalInfBrandRamp.current
}
