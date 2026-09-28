// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.utils

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.lucide.Cloud
import com.composables.icons.lucide.CloudDrizzle
import com.composables.icons.lucide.CloudFog
import com.composables.icons.lucide.CloudLightning
import com.composables.icons.lucide.CloudMoon
import com.composables.icons.lucide.CloudRain
import com.composables.icons.lucide.CloudSnow
import com.composables.icons.lucide.CloudSun
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Moon
import com.composables.icons.lucide.Sun
import com.paraskcd.influentiallauncher.glance.R
import com.paraskcd.influentiallauncher.weather.domain.model.WarningLevel
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherCondition
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherSourceName

object WeatherVisuals {
    fun iconOf(condition: WeatherCondition, isDay: Boolean): ImageVector = when (condition) {
        WeatherCondition.Clear -> if (isDay) Lucide.Sun else Lucide.Moon
        WeatherCondition.PartlyCloudy -> if (isDay) Lucide.CloudSun else Lucide.CloudMoon
        WeatherCondition.Cloudy -> Lucide.Cloud
        WeatherCondition.Fog -> Lucide.CloudFog
        WeatherCondition.Drizzle -> Lucide.CloudDrizzle
        WeatherCondition.Rain -> Lucide.CloudRain
        WeatherCondition.Snow -> Lucide.CloudSnow
        WeatherCondition.Thunder -> Lucide.CloudLightning
    }

    @StringRes
    fun labelOf(condition: WeatherCondition): Int = when (condition) {
        WeatherCondition.Clear -> R.string.glance_clear
        WeatherCondition.PartlyCloudy -> R.string.glance_partly_cloudy
        WeatherCondition.Cloudy -> R.string.glance_cloudy
        WeatherCondition.Fog -> R.string.glance_fog
        WeatherCondition.Drizzle -> R.string.glance_drizzle
        WeatherCondition.Rain -> R.string.glance_rain
        WeatherCondition.Snow -> R.string.glance_snow
        WeatherCondition.Thunder -> R.string.glance_thunder
    }

    @StringRes
    fun sourceOf(source: WeatherSourceName): Int = when (source) {
        WeatherSourceName.Aemet -> R.string.weather_source_aemet
        WeatherSourceName.OpenMeteo -> R.string.weather_source_open_meteo
    }

    @StringRes
    fun uvLabelOf(index: Int): Int = when {
        index <= 2 -> R.string.weather_uv_low
        index <= 5 -> R.string.weather_uv_moderate
        index <= 7 -> R.string.weather_uv_high
        index <= 10 -> R.string.weather_uv_very_high
        else -> R.string.weather_uv_extreme
    }

    @StringRes
    fun airLabelOf(europeanAqi: Int): Int = when {
        europeanAqi <= 20 -> R.string.weather_air_good
        europeanAqi <= 40 -> R.string.weather_air_fair
        europeanAqi <= 60 -> R.string.weather_air_moderate
        europeanAqi <= 80 -> R.string.weather_air_poor
        europeanAqi <= 100 -> R.string.weather_air_very_poor
        else -> R.string.weather_air_extremely_poor
    }

    fun warningArgbOf(level: WarningLevel): Long = when (level) {
        WarningLevel.Yellow -> 0xFFF5C518
        WarningLevel.Orange -> 0xFFF28C28
        WarningLevel.Red -> 0xFFE53935
    }
}
