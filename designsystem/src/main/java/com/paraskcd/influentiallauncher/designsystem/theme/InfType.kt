package com.paraskcd.influentiallauncher.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.paraskcd.influentiallauncher.designsystem.R

@OptIn(ExperimentalTextApi::class)
private fun quicksand(weight: Int) = Font(
    resId = R.font.quicksand,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight))
)

val Quicksand = FontFamily(quicksand(400), quicksand(500), quicksand(600), quicksand(700))

private fun quicksandStyle(weight: FontWeight, size: Int) =
    TextStyle(fontFamily = Quicksand, fontWeight = weight, fontSize = size.sp)

val InfTypography = Typography(
    displayLarge = quicksandStyle(FontWeight.Bold, 57),
    displayMedium = quicksandStyle(FontWeight.SemiBold, 45),
    displaySmall = quicksandStyle(FontWeight.Medium, 36),
    headlineLarge = quicksandStyle(FontWeight.SemiBold, 32),
    headlineMedium = quicksandStyle(FontWeight.Medium, 28),
    headlineSmall = quicksandStyle(FontWeight.Medium, 24),
    titleLarge = quicksandStyle(FontWeight.Medium, 22),
    titleMedium = quicksandStyle(FontWeight.Medium, 16),
    titleSmall = quicksandStyle(FontWeight.Medium, 14),
    bodyLarge = quicksandStyle(FontWeight.Normal, 16),
    bodyMedium = quicksandStyle(FontWeight.Normal, 14),
    bodySmall = quicksandStyle(FontWeight.Normal, 12),
    labelLarge = quicksandStyle(FontWeight.Medium, 14),
    labelMedium = quicksandStyle(FontWeight.Medium, 12),
    labelSmall = quicksandStyle(FontWeight.Medium, 11)
)
