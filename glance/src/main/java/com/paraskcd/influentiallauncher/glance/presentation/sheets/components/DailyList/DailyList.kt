// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.sheets.components.DailyList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.glance.presentation.sheets.components.DailyList.components.DayAccordion
import com.paraskcd.influentiallauncher.weather.domain.model.DayForecast

@Composable
fun DailyList(days: List<DayForecast>, modifier: Modifier = Modifier) {
    val lowest = days.minOf { it.minC }
    val highest = days.maxOf { it.maxC }
    Column(verticalArrangement = Arrangement.spacedBy(DsMetrics.groupGap), modifier = modifier.fillMaxWidth()) {
        days.forEachIndexed { index, day ->
            DayAccordion(day = day, index = index, count = days.size, lowest = lowest, highest = highest)
        }
    }
}
