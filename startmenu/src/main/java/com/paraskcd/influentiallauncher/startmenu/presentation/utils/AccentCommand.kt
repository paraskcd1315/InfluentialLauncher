// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.utils

object AccentCommand {
    private const val Key = "theme_customization_overlay_packages"

    fun set(seedHex: String): String {
        val json = buildString {
            append('{')
            append("\"android.theme.customization.system_palette\":\"").append(seedHex).append("\",")
            append("\"android.theme.customization.accent_color\":\"").append(seedHex).append("\",")
            append("\"android.theme.customization.color_source\":\"preset\",")
            append("\"android.theme.customization.theme_style\":\"TONAL_SPOT\",")
            append("\"android.theme.customization.seed_color_list\":[\"").append(seedHex).append("\"]")
            append('}')
        }
        return "settings put secure $Key '$json'"
    }

    fun fromWallpaper(): String =
        "settings put secure $Key '{\"android.theme.customization.color_source\":\"home_wallpaper\"}'"
}
