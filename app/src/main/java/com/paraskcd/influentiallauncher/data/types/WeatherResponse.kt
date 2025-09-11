package com.paraskcd.influentiallauncher.data.types

data class WeatherResponse(
    val current: CurrentWeather,
    val daily: DailyWeather
)
