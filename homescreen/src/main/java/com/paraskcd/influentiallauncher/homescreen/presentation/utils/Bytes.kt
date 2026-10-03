// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.homescreen.presentation.utils

object Bytes {
    private const val Kb = 1024.0
    private const val Mb = Kb * 1024
    private const val Gb = Mb * 1024

    fun format(value: Long): String = when {
        value >= Gb -> "%.2f GB".format(value / Gb)
        value >= Mb -> "%.1f MB".format(value / Mb)
        value >= Kb -> "%.0f KB".format(value / Kb)
        else -> "$value B"
    }
}
