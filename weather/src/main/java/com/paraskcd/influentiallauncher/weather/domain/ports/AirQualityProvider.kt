package com.paraskcd.influentiallauncher.weather.domain.ports

import com.paraskcd.influentiallauncher.weather.domain.model.AirQuality
import com.paraskcd.influentiallauncher.weather.domain.model.Place

interface AirQualityProvider {
    suspend fun airQuality(place: Place): AirQuality?
}
