package com.paraskcd.influentiallauncher.weather.domain.model

data class WeatherReport(
    val forecast: Forecast,
    val sources: List<WeatherSourceName>,
    val airQuality: AirQuality?,
    val warnings: List<WeatherWarning>
)
