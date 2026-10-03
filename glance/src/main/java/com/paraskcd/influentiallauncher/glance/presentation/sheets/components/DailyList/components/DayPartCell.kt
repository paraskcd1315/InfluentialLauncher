// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.sheets.components.DailyList.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.glance.R
import com.paraskcd.influentiallauncher.glance.presentation.utils.WeatherSheetMetrics
import com.paraskcd.influentiallauncher.glance.presentation.utils.WeatherVisuals
import com.paraskcd.influentiallauncher.glance.presentation.utils.windText
import com.paraskcd.influentiallauncher.weather.domain.model.DayPart

@Composable
fun DayPartCell(part: DayPart, modifier: Modifier = Modifier) {
    val colors = InfTheme.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(InfSpacing.s1),
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.weather_part_hours, part.startHour, part.endHour),
            style = MaterialTheme.typography.labelMedium,
            color = colors.textSecondary,
            maxLines = 1
        )
        Icon(
            imageVector = WeatherVisuals.iconOf(part.condition, part.isDay),
            contentDescription = stringResource(WeatherVisuals.labelOf(part.condition)),
            tint = colors.textPrimary,
            modifier = Modifier.size(WeatherSheetMetrics.partIcon)
        )
        Text(
            text = part.rainChancePercent?.let { stringResource(R.string.weather_percent, it) }.orEmpty(),
            style = MaterialTheme.typography.labelMedium,
            color = colors.brandText,
            maxLines = 1
        )
        Text(
            text = part.windKmh?.let { windText(it, part.windFrom) }.orEmpty(),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
            maxLines = 1
        )
    }
}
