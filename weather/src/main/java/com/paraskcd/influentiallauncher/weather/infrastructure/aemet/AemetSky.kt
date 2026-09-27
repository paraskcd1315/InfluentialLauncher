package com.paraskcd.influentiallauncher.weather.infrastructure.aemet

import com.paraskcd.influentiallauncher.weather.domain.model.WeatherCondition

object AemetSky {
    fun conditionOf(code: String): WeatherCondition {
        val base = code.trimEnd('n', 'N').toIntOrNull() ?: return WeatherCondition.Cloudy
        return when (base) {
            11 -> WeatherCondition.Clear
            12, 13, 17 -> WeatherCondition.PartlyCloudy
            14, 15, 16 -> WeatherCondition.Cloudy
            in 23..27 -> WeatherCondition.Rain
            in 43..46 -> WeatherCondition.Drizzle
            in 33..36, in 71..74 -> WeatherCondition.Snow
            in 51..54, in 61..64 -> WeatherCondition.Thunder
            in 81..83 -> WeatherCondition.Fog
            else -> WeatherCondition.Cloudy
        }
    }

    fun isNight(code: String): Boolean = code.endsWith('n', ignoreCase = true)
}
