// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.domain.ports

import com.paraskcd.influentiallauncher.weather.domain.model.Forecast
import com.paraskcd.influentiallauncher.weather.domain.model.Place
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherSourceName

interface WeatherProvider {
    val name: WeatherSourceName

    fun covers(place: Place): Boolean

    suspend fun forecast(place: Place): Forecast?
}
