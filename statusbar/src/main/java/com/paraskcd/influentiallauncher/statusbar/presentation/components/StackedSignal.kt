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
    Canvas(
        modifier = modifier.size(
            width = StatusBarMetrics.iconSize * StatusBarMetrics.stackedWidth,
            height = StatusBarMetrics.iconSize
        ),
        contentDescription = contentDescription
    ) {
        val stroke = size.height * StatusBarMetrics.stackedStroke
        val inset = size.height * StatusBarMetrics.stackedInset
        val steps = StatusBarMetrics.stackedColumns - 1
        val pitch = size.height * StatusBarMetrics.stackedPitch
        val barBottom = size.height * StatusBarMetrics.stackedBarBottom
        val shortest = size.height * StatusBarMetrics.stackedBarShortest
        val tallest = size.height * StatusBarMetrics.stackedBarTallest
        val dotCentre = size.height * StatusBarMetrics.stackedDotCentre
        repeat(StatusBarMetrics.stackedColumns) { index ->
            val left = inset + index * pitch
            val barHeight = shortest + (tallest - shortest) * index / steps
            drawRoundRect(
                color = color,
                topLeft = Offset(left, barBottom - barHeight),
                size = Size(stroke, barHeight),
                cornerRadius = CornerRadius(stroke / 2, stroke / 2),
                alpha = if (index < primaryBars) 1f else StatusBarMetrics.stackedUnlitAlpha
            )
            drawCircle(
                color = color,
                radius = stroke / 2,
                center = Offset(left + stroke / 2, dotCentre),
                alpha = if (index < secondaryBars) 1f else StatusBarMetrics.stackedUnlitAlpha
            )
        }
    }
}
