package com.paraskcd.influentiallauncher.data.types

data class CurrentWeather(
    val time: String,
    val temperature_2m: Double,
    val weather_code: Int,
    val wind_speed_10m: Double,
    val relative_humidity_2m: Int
)
