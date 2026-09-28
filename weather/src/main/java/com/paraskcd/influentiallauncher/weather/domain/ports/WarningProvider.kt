// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.domain.ports

import com.paraskcd.influentiallauncher.weather.domain.model.Place
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherWarning

interface WarningProvider {
    fun covers(place: Place): Boolean

    suspend fun warnings(place: Place): List<WeatherWarning>
}
