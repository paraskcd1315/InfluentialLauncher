// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.domain.model

import java.time.OffsetDateTime

data class WeatherWarning(
    val headline: String,
    val description: String?,
    val areas: List<String>,
    val level: WarningLevel,
    val onset: OffsetDateTime?,
    val expires: OffsetDateTime?
)
