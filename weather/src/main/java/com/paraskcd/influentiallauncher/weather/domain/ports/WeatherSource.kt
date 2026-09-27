package com.paraskcd.influentiallauncher.weather.domain.ports

import com.paraskcd.influentiallauncher.weather.domain.model.Forecast
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherReport
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherSourceName

interface WeatherSource {
    val permission: String

    fun hasPermission(): Boolean

    suspend fun forecast(force: Boolean = false): Forecast?

    suspend fun report(source: WeatherSourceName? = null, force: Boolean = false): WeatherReport?
}
