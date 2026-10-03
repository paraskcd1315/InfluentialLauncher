// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.utils

import androidx.compose.ui.graphics.Color

object AccentPalette {
    val seeds: List<String> = listOf(
        "1A73E8",
        "00897B",
        "2E7D32",
        "F9A825",
        "EF6C00",
        "C62828",
        "AD1457",
        "6A1B9A"
    )

    fun color(seedHex: String): Color = Color("FF$seedHex".toLong(16))
}
