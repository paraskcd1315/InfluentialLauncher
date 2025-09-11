package com.paraskcd.influentiallauncher.data.types

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val weather: WeatherResponse, val cityName: String? = null) : WeatherState()
    data class Error(val message: String) : WeatherState()
    object PermissionRequired : WeatherState()
}