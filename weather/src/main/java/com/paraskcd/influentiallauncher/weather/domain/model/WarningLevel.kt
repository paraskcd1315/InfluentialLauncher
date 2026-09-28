// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.domain.model

enum class WarningLevel {
    Yellow,
    Orange,
    Red;

    companion object {
        fun fromAwareness(value: String): WarningLevel? = when (value.substringBefore(';').trim()) {
            "2" -> Yellow
            "3" -> Orange
            "4" -> Red
            else -> null
        }
    }
}
