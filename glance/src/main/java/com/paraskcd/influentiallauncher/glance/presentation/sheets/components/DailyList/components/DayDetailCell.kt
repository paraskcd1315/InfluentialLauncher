// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.sheets.components.DailyList.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.glance.presentation.model.DayDetailItem
import com.paraskcd.influentiallauncher.glance.presentation.utils.WeatherSheetMetrics

@Composable
fun DayDetailCell(item: DayDetailItem, modifier: Modifier = Modifier) {
    val colors = InfTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(InfSpacing.s1), modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(InfSpacing.s1), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = item.icon, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(WeatherSheetMetrics.detailIcon))
            Text(
                text = item.label,
                style = MaterialTheme.typography.labelMedium,
                color = colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(text = item.value, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary, maxLines = 1)
    }
}
