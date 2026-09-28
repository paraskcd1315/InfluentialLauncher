// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.theme

import androidx.compose.ui.graphics.Color

fun infDarkColors(ramp: BrandRamp) = InfColors(
    isLight = false,
    brand = ramp.s500,
    brandText = ramp.s300,
    brandTint = ramp.s500.copy(alpha = 0.22f),
    onBrand = InfContrast.contentOn(ramp.s500),
    bgBase = InfDarkBgBase,
    surface = InfDarkSurface,
    surfaceContainer = InfDarkSurfaceContainer,
    surfaceContainerHigh = InfDarkSurfaceContainerHigh,
    textPrimary = Color.White.copy(alpha = 0.92f),
    textSecondary = Color.White.copy(alpha = 0.58f),
    textTertiary = Color.White.copy(alpha = 0.40f),
    glassBg = Color.White.copy(alpha = 0.06f),
    glassStrongBg = InfDarkSurfaceContainerHigh.copy(alpha = 0.80f),
    glassBorder = Color.White.copy(alpha = 0.12f),
    glassSpecular = Color.White.copy(alpha = 0.16f),
    border = Color.White.copy(alpha = 0.14f),
    hairline = Color.White.copy(alpha = 0.10f),
    scrim = Color.Black.copy(alpha = 0.52f),
    success = InfSuccess,
    warning = InfWarning,
    danger = InfDanger,
    dangerText = InfDangerTextDark
)

fun infLightColors(ramp: BrandRamp) = InfColors(
    isLight = true,
    brand = ramp.s500,
    brandText = ramp.s600,
    brandTint = ramp.s500.copy(alpha = 0.14f),
    onBrand = InfContrast.contentOn(ramp.s500),
    bgBase = InfLightBgBase,
    surface = InfLightSurface,
    surfaceContainer = InfLightSurfaceContainer,
    surfaceContainerHigh = InfLightSurfaceContainerHigh,
    textPrimary = InfInkLight.copy(alpha = 0.92f),
    textSecondary = InfInkLight.copy(alpha = 0.56f),
    textTertiary = InfInkLight.copy(alpha = 0.40f),
    glassBg = Color.White.copy(alpha = 0.55f),
    glassStrongBg = Color.White.copy(alpha = 0.65f),
    glassBorder = InfBorderLight.copy(alpha = 0.12f),
    glassSpecular = Color.White.copy(alpha = 0.85f),
    border = InfBorderLight.copy(alpha = 0.14f),
    hairline = Color.Black.copy(alpha = 0.08f),
    scrim = Color.Black.copy(alpha = 0.40f),
    success = InfSuccess,
    warning = InfWarning,
    danger = InfDanger,
    dangerText = InfDangerTextLight
)
