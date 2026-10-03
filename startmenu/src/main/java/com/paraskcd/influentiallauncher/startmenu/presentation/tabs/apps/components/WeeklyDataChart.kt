// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.apps.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.tasks.domain.model.DayData
import java.time.Instant
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeeklyDataChart(days: List<DayData>, modifier: Modifier = Modifier) {
    val colors = InfTheme.colors
    val max = (days.maxOfOrNull { maxOf(it.mobileBytes, it.wifiBytes) } ?: 0L).coerceAtLeast(1L)
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(InfSpacing.s3)) {
        Row(
            modifier = Modifier.fillMaxWidth().height(ChartHeight),
            horizontalArrangement = Arrangement.spacedBy(InfSpacing.s2),
            verticalAlignment = Alignment.Bottom
        ) {
            days.forEach { day ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(InfSpacing.s1)
                ) {
                    Row(
                        modifier = Modifier.height(BarArea),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Bar(fraction = day.mobileBytes.toFloat() / max, color = colors.brand)
                        Bar(fraction = day.wifiBytes.toFloat() / max, color = colors.success)
                    }
                    Text(
                        text = dayInitial(day.dayStart),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(InfSpacing.s4)) {
            Legend(color = colors.brand, label = stringResource(R.string.startmenu_data_mobile))
            Legend(color = colors.success, label = stringResource(R.string.startmenu_data_wifi))
        }
    }
}

@Composable
private fun Bar(fraction: Float, color: Color) {
    Box(
        modifier = Modifier
            .width(BarWidth)
            .height(BarArea * fraction.coerceIn(0f, 1f))
            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
            .background(color)
    )
}

@Composable
private fun Legend(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(InfSpacing.s1)) {
        Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(color))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = InfTheme.colors.textSecondary)
    }
}

private fun dayInitial(epochMillis: Long): String =
    Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).dayOfWeek
        .getDisplayName(TextStyle.NARROW, Locale.getDefault())

private val ChartHeight = 140.dp
private val BarArea = 116.dp
private val BarWidth = 9.dp
