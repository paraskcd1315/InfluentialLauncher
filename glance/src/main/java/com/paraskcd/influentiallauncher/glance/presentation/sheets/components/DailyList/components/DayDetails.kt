// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.sheets.components.DailyList.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.glance.R
import com.paraskcd.influentiallauncher.glance.presentation.utils.WeatherSheetMetrics
import com.paraskcd.influentiallauncher.glance.presentation.utils.dayDetailItems
import com.paraskcd.influentiallauncher.weather.domain.model.DayForecast

@Composable
fun DayDetails(day: DayForecast, modifier: Modifier = Modifier) {
    val items = dayDetailItems(day)
    Column(verticalArrangement = Arrangement.spacedBy(InfSpacing.s4), modifier = modifier.fillMaxWidth()) {
        if (items.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(InfSpacing.s3)) {
                items.chunked(WeatherSheetMetrics.detailColumns).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(InfSpacing.s3)) {
                        row.forEach { item -> DayDetailCell(item = item, modifier = Modifier.weight(1f)) }
                        repeat(WeatherSheetMetrics.detailColumns - row.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
            }
        }
        if (day.parts.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(InfSpacing.s2)) {
                Text(
                    text = stringResource(R.string.weather_day_parts),
                    style = MaterialTheme.typography.labelMedium,
                    color = InfTheme.colors.textSecondary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(InfSpacing.s2), modifier = Modifier.fillMaxWidth()) {
                    day.parts.forEach { part -> DayPartCell(part = part, modifier = Modifier.weight(1f)) }
                }
            }
        }
    }
}
