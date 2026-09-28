// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.domain.model

import java.time.LocalDateTime

data class HourForecast(
    val time: LocalDateTime,
    val temperatureC: Int,
    val condition: WeatherCondition,
    val isDay: Boolean,
    val rainChancePercent: Int?
)
