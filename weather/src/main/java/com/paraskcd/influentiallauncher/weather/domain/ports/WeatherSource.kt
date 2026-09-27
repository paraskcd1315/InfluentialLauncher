package com.paraskcd.influentiallauncher.weather.domain.ports

import com.paraskcd.influentiallauncher.weather.domain.model.Weather

interface WeatherSource {
    val permission: String

    fun hasPermission(): Boolean

    suspend fun current(force: Boolean = false): Weather?
}
