package com.paraskcd.influentiallauncher.weather.domain.ports

import com.paraskcd.influentiallauncher.weather.domain.model.Forecast
import com.paraskcd.influentiallauncher.weather.domain.model.Place
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherSourceName

interface WeatherProvider {
    val name: WeatherSourceName

    fun covers(place: Place): Boolean

    suspend fun forecast(place: Place): Forecast?
}
