// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class DayForecast(
    val date: LocalDate,
    val minC: Int,
    val maxC: Int,
    val condition: WeatherCondition,
    val rainChancePercent: Int?,
    val feelsLikeMinC: Int? = null,
    val feelsLikeMaxC: Int? = null,
    val humidityMinPercent: Int? = null,
    val humidityMaxPercent: Int? = null,
    val windKmh: Int? = null,
    val windFrom: CompassPoint? = null,
    val gustKmh: Int? = null,
    val rainMm: Double? = null,
    val uvIndex: Int? = null,
    val sunrise: LocalTime? = null,
    val sunset: LocalTime? = null,
    val snowLevelM: Int? = null,
    val parts: List<DayPart> = emptyList()
)
