// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.domain.model

data class WeatherReport(
    val place: Place,
    val forecast: Forecast,
    val sources: List<WeatherSourceName>,
    val airQuality: AirQuality?,
    val warnings: List<WeatherWarning>
)
