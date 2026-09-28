// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.sheets.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.paraskcd.influentiallauncher.designsystem.foundation.horizontalFadingEdges
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.glance.R
import com.paraskcd.influentiallauncher.glance.presentation.utils.WeatherSheetMetrics
import com.paraskcd.influentiallauncher.glance.presentation.utils.WeatherVisuals
import com.paraskcd.influentiallauncher.weather.domain.model.HourForecast
import java.time.format.DateTimeFormatter

private val HourFormat = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun HourlyRow(hours: List<HourForecast>, horizontalInset: Dp) {
    val colors = InfTheme.colors
    val state = rememberLazyListState()
    LazyRow(
        state = state,
        contentPadding = PaddingValues(horizontal = horizontalInset),
        horizontalArrangement = Arrangement.spacedBy(InfSpacing.s2),
        modifier = Modifier
            .fillMaxWidth()
            .horizontalFadingEdges(state)
    ) {
        items(hours, key = { it.time.toString() }) { hour ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(InfSpacing.s1),
                modifier = Modifier.width(WeatherSheetMetrics.hourWidth)
            ) {
                Text(
                    text = if (hour == hours.first()) stringResource(R.string.weather_now) else hour.time.format(HourFormat),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textSecondary
                )
                Icon(
                    imageVector = WeatherVisuals.iconOf(hour.condition, hour.isDay),
                    contentDescription = stringResource(WeatherVisuals.labelOf(hour.condition)),
                    tint = colors.textPrimary,
                    modifier = Modifier.size(WeatherSheetMetrics.hourIcon)
                )
                Text(
                    text = stringResource(R.string.glance_temperature, hour.temperatureC),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary
                )
                val rain = hour.rainChancePercent?.takeIf { it >= WeatherSheetMetrics.rainShownFromPercent }
                Text(
                    text = rain?.let { stringResource(R.string.weather_percent, it) }.orEmpty(),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.brandText
                )
            }
        }
    }
}
