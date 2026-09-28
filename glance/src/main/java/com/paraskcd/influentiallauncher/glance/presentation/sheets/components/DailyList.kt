// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.sheets.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.glance.R
import com.paraskcd.influentiallauncher.glance.presentation.utils.WeatherSheetMetrics
import com.paraskcd.influentiallauncher.glance.presentation.utils.WeatherVisuals
import com.paraskcd.influentiallauncher.weather.domain.model.DayForecast
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DayFormat = DateTimeFormatter.ofPattern("EEE")

@Composable
fun DailyList(days: List<DayForecast>, modifier: Modifier = Modifier) {
    val colors = InfTheme.colors
    val lowest = days.minOf { it.minC }
    val highest = days.maxOf { it.maxC }
    val today = LocalDate.now()
    Column(verticalArrangement = Arrangement.spacedBy(DsMetrics.groupGap), modifier = modifier.fillMaxWidth()) {
        days.forEachIndexed { index, day ->
            InfGroupedCard(index = index, count = days.size) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(InfSpacing.s3),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = InfSpacing.s4, vertical = InfSpacing.s3)
                ) {
                    Text(
                        text = if (day.date == today) stringResource(R.string.weather_today) else day.date.format(DayFormat),
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.textPrimary,
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
                        modifier = Modifier.width(WeatherSheetMetrics.dayRainWidth)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(R.string.glance_temperature, day.minC),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary,
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(WeatherSheetMetrics.dayTemperatureWidth)
                    )
                    RangeBar(low = day.minC, high = day.maxC, lowest = lowest, highest = highest)
                    Text(
                        text = stringResource(R.string.glance_temperature, day.maxC),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textPrimary,
                        modifier = Modifier.width(WeatherSheetMetrics.dayTemperatureWidth)
                    )
                }
            }
        }
    }
}

@Composable
private fun RangeBar(low: Int, high: Int, lowest: Int, highest: Int) {
    val track = InfTheme.colors.border
    val fill = InfTheme.colors.brand
    val span = (highest - lowest).coerceAtLeast(1).toFloat()
    val start = (low - lowest) / span
    val end = (high - lowest) / span
    Spacer(
        modifier = Modifier
            .width(WeatherSheetMetrics.rangeBarWidth)
            .height(WeatherSheetMetrics.rangeBarHeight)
            .drawBehind {
                val radius = CornerRadius(size.height / 2, size.height / 2)
                drawRoundRect(color = track, cornerRadius = radius)
                drawRoundRect(
                    color = fill,
                    topLeft = Offset(size.width * start, 0f),
                    size = Size((size.width * (end - start)).coerceAtLeast(size.height), size.height),
                    cornerRadius = radius
                )
            }
    )
}
