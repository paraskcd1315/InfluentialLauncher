// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.statusbar.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.paraskcd.influentiallauncher.statusbar.presentation.utils.StatusBarMetrics

@Composable
fun StackedSignal(
    primaryBars: Int,
    secondaryBars: Int,
    color: Color,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(StatusBarMetrics.iconSize), contentDescription = contentDescription) {
        val column = size.width * StatusBarMetrics.stackedColumnWidth
        val pitch = (size.width - column) / (StatusBarMetrics.stackedColumns - 1)
        val barTop = size.height * StatusBarMetrics.stackedBarTop
        val barHeight = size.height * StatusBarMetrics.stackedBarHeight
        val dotCentre = size.height * StatusBarMetrics.stackedDotTop + column / 2
        repeat(StatusBarMetrics.stackedColumns) { index ->
            val left = index * pitch
            drawRoundRect(
                color = color,
                topLeft = Offset(left, barTop),
                size = Size(column, barHeight),
                cornerRadius = CornerRadius(column / 2, column / 2),
                alpha = if (index < primaryBars) 1f else StatusBarMetrics.stackedUnlitAlpha
            )
            drawCircle(
                color = color,
                radius = column / 2,
                center = Offset(left + column / 2, dotCentre),
                alpha = if (index < secondaryBars) 1f else StatusBarMetrics.stackedUnlitAlpha
            )
        }
    }
}
