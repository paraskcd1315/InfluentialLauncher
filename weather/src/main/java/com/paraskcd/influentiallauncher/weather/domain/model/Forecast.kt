// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.domain.model

import java.time.LocalTime

data class Forecast(
    val now: Weather,
    val feelsLikeC: Int?,
    val humidityPercent: Int?,
    val windKmh: Int?,
    val rainChancePercent: Int?,
    val rainTodayMm: Double?,
    val uvIndex: Int?,
    val sunrise: LocalTime?,
    val sunset: LocalTime?,
    val hours: List<HourForecast>,
    val days: List<DayForecast>,
    val source: WeatherSourceName
)
