package com.paraskcd.influentiallauncher.weather.domain.model

import java.time.LocalDate

data class DayForecast(
    val date: LocalDate,
    val minC: Int,
    val maxC: Int,
    val condition: WeatherCondition,
    val rainChancePercent: Int?
)
