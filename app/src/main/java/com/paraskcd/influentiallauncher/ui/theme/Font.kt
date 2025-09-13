package com.paraskcd.influentiallauncher.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.paraskcd.influentiallauncher.R

private val quicksand = GoogleFont("Quicksand")

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val QuicksandFontFamily = FontFamily(
    Font(googleFont = quicksand, fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = quicksand, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = quicksand, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = quicksand, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = quicksand, fontProvider = provider, weight = FontWeight.Bold),
)