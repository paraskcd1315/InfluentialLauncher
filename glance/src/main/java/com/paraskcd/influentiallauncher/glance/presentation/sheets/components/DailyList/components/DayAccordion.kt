// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.sheets.components.DailyList.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.paraskcd.influentiallauncher.designsystem.organisms.InfAccordionCard
import com.paraskcd.influentiallauncher.glance.R
import com.paraskcd.influentiallauncher.glance.presentation.utils.WeatherFormats
import com.paraskcd.influentiallauncher.weather.domain.model.DayForecast
import java.time.LocalDate

@Composable
fun DayAccordion(day: DayForecast, index: Int, count: Int, lowest: Int, highest: Int) {
    var expanded by rememberSaveable(day.date.toString()) { mutableStateOf(false) }
    val dayLabel = if (day.date == LocalDate.now()) stringResource(R.string.weather_today) else day.date.format(WeatherFormats.weekDay)
    InfAccordionCard(
        index = index,
        count = count,
        expanded = expanded,
        onToggle = { expanded = !expanded },
        toggleLabel = stringResource(if (expanded) R.string.weather_day_collapse else R.string.weather_day_expand, dayLabel),
        header = { DaySummary(day = day, dayLabel = dayLabel, lowest = lowest, highest = highest) },
        content = { DayDetails(day = day) }
    )
}
