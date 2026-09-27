package com.paraskcd.influentiallauncher.glance.presentation.model

import com.paraskcd.influentiallauncher.weather.domain.model.Weather

sealed interface WeatherLoad {
    data object Loading : WeatherLoad

    data class Ready(val weather: Weather?) : WeatherLoad
}
