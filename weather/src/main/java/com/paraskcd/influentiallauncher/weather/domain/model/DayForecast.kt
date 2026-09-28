// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.domain.model

import java.time.LocalDate

data class DayForecast(
    val date: LocalDate,
    val minC: Int,
    val maxC: Int,
    val condition: WeatherCondition,
    val rainChancePercent: Int?
)
