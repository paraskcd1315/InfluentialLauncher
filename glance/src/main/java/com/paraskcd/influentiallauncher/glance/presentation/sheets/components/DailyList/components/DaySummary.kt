// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.sheets.components.DailyList.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.glance.R
import com.paraskcd.influentiallauncher.glance.presentation.utils.WeatherSheetMetrics
import com.paraskcd.influentiallauncher.glance.presentation.utils.WeatherVisuals
import com.paraskcd.influentiallauncher.weather.domain.model.DayForecast

@Composable
fun RowScope.DaySummary(day: DayForecast, dayLabel: String, lowest: Int, highest: Int) {
    val colors = InfTheme.colors
    Text(
        text = dayLabel,
        style = MaterialTheme.typography.titleSmall,
        color = colors.textPrimary,
        maxLines = 1,
        modifier = Modifier.width(WeatherSheetMetrics.dayLabelWidth)
    )
    Icon(
        imageVector = WeatherVisuals.iconOf(day.condition, isDay = true),
        contentDescription = stringResource(WeatherVisuals.labelOf(day.condition)),
        tint = colors.textPrimary,
        modifier = Modifier.size(WeatherSheetMetrics.dayIcon)
    )
    val rain = day.rainChancePercent?.takeIf { it >= WeatherSheetMetrics.rainShownFromPercent }
    Text(
        text = rain?.let { stringResource(R.string.weather_percent, it) }.orEmpty(),
        style = MaterialTheme.typography.labelMedium,
        color = colors.brandText,
        maxLines = 1,
        modifier = Modifier.width(WeatherSheetMetrics.dayRainWidth)
    )
    Text(
        text = stringResource(R.string.glance_temperature, day.minC),
        style = MaterialTheme.typography.bodyMedium,
        color = colors.textSecondary,
        textAlign = TextAlign.End,
        maxLines = 1,
        modifier = Modifier.width(WeatherSheetMetrics.dayTemperatureWidth)
    )
    RangeBar(low = day.minC, high = day.maxC, lowest = lowest, highest = highest, modifier = Modifier.weight(1f))
    Text(
        text = stringResource(R.string.glance_temperature, day.maxC),
        style = MaterialTheme.typography.bodyMedium,
        color = colors.textPrimary,
        maxLines = 1,
        modifier = Modifier.width(WeatherSheetMetrics.dayTemperatureWidth)
    )
}
