package com.paraskcd.influentiallauncher.weather.domain.model

data class Weather(
    val temperatureC: Int,
    val condition: WeatherCondition,
    val isDay: Boolean,
    val place: String?
)
