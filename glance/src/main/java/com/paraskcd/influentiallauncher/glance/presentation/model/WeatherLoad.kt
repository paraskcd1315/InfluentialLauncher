// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.model

import com.paraskcd.influentiallauncher.weather.domain.model.Weather

sealed interface WeatherLoad {
    data object Loading : WeatherLoad

    data class Ready(val weather: Weather?) : WeatherLoad
}
