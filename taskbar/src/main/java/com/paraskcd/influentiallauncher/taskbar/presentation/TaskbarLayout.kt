// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.taskbar.presentation

import androidx.compose.ui.unit.Dp
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics

object TaskbarLayout {
    const val widthFraction: Float = TaskbarMetrics.widthFraction
    const val heightFraction: Float = TaskbarMetrics.heightFraction
    val thickness: Dp = TaskbarMetrics.barHeight
    val gap: Dp = TaskbarMetrics.aboveGap
}
