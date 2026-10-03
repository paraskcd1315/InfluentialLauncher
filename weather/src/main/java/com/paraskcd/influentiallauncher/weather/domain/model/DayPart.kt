// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.domain.model

data class DayPart(
    val startHour: Int,
    val endHour: Int,
    val condition: WeatherCondition,
    val isDay: Boolean,
    val rainChancePercent: Int?,
    val windKmh: Int?,
    val windFrom: CompassPoint?
)
