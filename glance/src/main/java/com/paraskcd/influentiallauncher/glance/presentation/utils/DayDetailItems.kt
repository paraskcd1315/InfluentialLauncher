// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.CloudRain
import com.composables.icons.lucide.Droplets
import com.composables.icons.lucide.Gauge
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Snowflake
import com.composables.icons.lucide.Sun
import com.composables.icons.lucide.Sunrise
import com.composables.icons.lucide.Sunset
import com.composables.icons.lucide.Thermometer
import com.composables.icons.lucide.Umbrella
import com.composables.icons.lucide.Wind
import com.paraskcd.influentiallauncher.glance.R
import com.paraskcd.influentiallauncher.glance.presentation.model.DayDetailItem
import com.paraskcd.influentiallauncher.weather.domain.model.CompassPoint
import com.paraskcd.influentiallauncher.weather.domain.model.DayForecast

@Composable
fun dayDetailItems(day: DayForecast): List<DayDetailItem> = buildList {
    rangeOf(day.feelsLikeMinC, day.feelsLikeMaxC)?.let { (low, high) ->
        add(DayDetailItem(Lucide.Thermometer, stringResource(R.string.weather_feels_like_label), stringResource(R.string.weather_temperature_range, low, high)))
    }
    rangeOf(day.humidityMinPercent, day.humidityMaxPercent)?.let { (low, high) ->
        val value = if (low == high) stringResource(R.string.weather_percent, low) else stringResource(R.string.weather_percent_range, low, high)
        add(DayDetailItem(Lucide.Droplets, stringResource(R.string.weather_humidity), value))
    }
    day.windKmh?.let { add(DayDetailItem(Lucide.Wind, stringResource(R.string.weather_wind), windText(it, day.windFrom))) }
    day.gustKmh?.let { add(DayDetailItem(Lucide.Gauge, stringResource(R.string.weather_gusts), stringResource(R.string.weather_speed, it))) }
    day.rainChancePercent?.let { add(DayDetailItem(Lucide.Umbrella, stringResource(R.string.weather_rain_probability), stringResource(R.string.weather_percent, it))) }
    day.rainMm?.let { add(DayDetailItem(Lucide.CloudRain, stringResource(R.string.weather_rainfall), stringResource(R.string.weather_mm, it))) }
    day.uvIndex?.let {
        add(DayDetailItem(Lucide.Sun, stringResource(R.string.weather_uv), stringResource(R.string.weather_uv_value, it, stringResource(WeatherVisuals.uvLabelOf(it)))))
    }
    day.sunrise?.let { add(DayDetailItem(Lucide.Sunrise, stringResource(R.string.weather_sunrise), it.format(WeatherFormats.clock))) }
    day.sunset?.let { add(DayDetailItem(Lucide.Sunset, stringResource(R.string.weather_sunset), it.format(WeatherFormats.clock))) }
    day.snowLevelM?.let { add(DayDetailItem(Lucide.Snowflake, stringResource(R.string.weather_snow_level), stringResource(R.string.weather_metres, it))) }
}

@Composable
fun windText(kmh: Int, from: CompassPoint?): String =
    if (from == null) stringResource(R.string.weather_speed, kmh)
    else stringResource(R.string.weather_speed_from, kmh, stringResource(WeatherVisuals.compassOf(from)))

private fun rangeOf(low: Int?, high: Int?): Pair<Int, Int>? = if (low == null || high == null) null else low to high
