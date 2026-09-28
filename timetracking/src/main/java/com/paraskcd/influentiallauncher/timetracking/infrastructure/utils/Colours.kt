// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.infrastructure.utils

object Colours {
    fun parse(value: String?): Int? {
        val hex = value?.trim()?.removePrefix("#") ?: return null
        val rgb = when (hex.length) {
            6 -> hex
            3 -> hex.map { "$it$it" }.joinToString("")
            else -> return null
        }
        return rgb.toLongOrNull(16)?.let { (0xFF000000 or it).toInt() }
    }
}
