package com.paraskcd.influentiallauncher.weather.domain.model

data class AirQuality(
    val europeanAqi: Int,
    val pm25: Double?,
    val pm10: Double?
)
