// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.sheets.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.Droplets
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Umbrella
import com.composables.icons.lucide.Wind
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.glance.R
import com.paraskcd.influentiallauncher.glance.presentation.utils.WeatherSheetMetrics
import com.paraskcd.influentiallauncher.glance.presentation.utils.WeatherVisuals
import com.paraskcd.influentiallauncher.weather.domain.model.DayForecast
import com.paraskcd.influentiallauncher.weather.domain.model.Forecast

@Composable
fun WeatherNow(forecast: Forecast, today: DayForecast?, modifier: Modifier = Modifier) {
    val colors = InfTheme.colors
    val now = forecast.now
    Column(verticalArrangement = Arrangement.spacedBy(InfSpacing.s4), modifier = modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(InfSpacing.s4),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = WeatherVisuals.iconOf(now.condition, now.isDay),
                contentDescription = null,
                tint = colors.textPrimary,
                modifier = Modifier.size(WeatherSheetMetrics.nowIcon)
            )
            Text(
                text = stringResource(R.string.glance_temperature, now.temperatureC),
                style = MaterialTheme.typography.displayMedium,
                color = colors.textPrimary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(WeatherVisuals.labelOf(now.condition)),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary
                )
                today?.let {
                    Text(
                        text = stringResource(R.string.weather_high_low, it.maxC, it.minC),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                }
                forecast.feelsLikeC?.let {
                    Text(
                        text = stringResource(R.string.weather_feels_like, it),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(InfSpacing.s3)) {
            WeatherTile(
                icon = Lucide.Droplets,
                label = stringResource(R.string.weather_humidity),
                value = forecast.humidityPercent?.let { stringResource(R.string.weather_percent, it) },
                modifier = Modifier.weight(1f)
            )
            WeatherTile(
                icon = Lucide.Wind,
                label = stringResource(R.string.weather_wind),
                value = forecast.windKmh?.let { stringResource(R.string.weather_speed, it) },
                modifier = Modifier.weight(1f)
            )
            WeatherTile(
                icon = Lucide.Umbrella,
                label = stringResource(R.string.weather_rain_chance),
                value = forecast.rainChancePercent?.let { stringResource(R.string.weather_percent, it) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
