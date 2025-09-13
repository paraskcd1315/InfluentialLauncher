package com.paraskcd.influentiallauncher.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = QuicksandFontFamily, fontWeight = FontWeight.Bold, fontSize = 57.sp),
    displayMedium = TextStyle(fontFamily = QuicksandFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 45.sp),
    displaySmall = TextStyle(fontFamily = QuicksandFontFamily, fontWeight = FontWeight.Medium, fontSize = 36.sp),

    headlineLarge = TextStyle(fontFamily = QuicksandFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 32.sp),
    headlineMedium = TextStyle(fontFamily = QuicksandFontFamily, fontWeight = FontWeight.Medium, fontSize = 28.sp),
    headlineSmall = TextStyle(fontFamily = QuicksandFontFamily, fontWeight = FontWeight.Medium, fontSize = 24.sp),

    titleLarge = TextStyle(fontFamily = QuicksandFontFamily, fontWeight = FontWeight.Medium, fontSize = 22.sp),
    titleMedium = TextStyle(fontFamily = QuicksandFontFamily, fontWeight = FontWeight.Medium, fontSize = 16.sp),
    titleSmall = TextStyle(fontFamily = QuicksandFontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp),

    bodyLarge = TextStyle(fontFamily = QuicksandFontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = QuicksandFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = QuicksandFontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp),

    labelLarge = TextStyle(fontFamily = QuicksandFontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = QuicksandFontFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp),
    labelSmall = TextStyle(fontFamily = QuicksandFontFamily, fontWeight = FontWeight.Medium, fontSize = 11.sp),
)