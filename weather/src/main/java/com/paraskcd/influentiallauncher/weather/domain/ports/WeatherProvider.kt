package com.paraskcd.influentiallauncher.weather.domain.ports

import com.paraskcd.influentiallauncher.weather.domain.model.Place
import com.paraskcd.influentiallauncher.weather.domain.model.Weather

interface WeatherProvider {
    fun covers(place: Place): Boolean

    suspend fun current(place: Place): Weather?
}
