// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.sheets.components.DailyList.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.glance.presentation.utils.WeatherSheetMetrics

@Composable
fun RangeBar(low: Int, high: Int, lowest: Int, highest: Int, modifier: Modifier = Modifier) {
    val track = InfTheme.colors.border
    val fill = InfTheme.colors.brand
    val span = (highest - lowest).coerceAtLeast(1).toFloat()
    val start = (low - lowest) / span
    val end = (high - lowest) / span
    Spacer(
        modifier = modifier
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
