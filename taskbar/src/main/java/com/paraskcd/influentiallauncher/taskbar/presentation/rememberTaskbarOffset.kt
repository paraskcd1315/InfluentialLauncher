// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.taskbar.presentation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics
import com.paraskcd.influentiallauncher.windowing.presentation.isLandscape

@Composable
fun rememberTaskbarOffset(): Dp {
    val density = LocalDensity.current
    val direction = LocalLayoutDirection.current
    if (isLandscape()) {
        val side = with(density) { WindowInsets.navigationBars.union(WindowInsets.displayCutout).getRight(density, direction).toDp() }
        return maxOf(TaskbarMetrics.sideDistance, side + TaskbarMetrics.navigationGap)
    }
    val navigationBar = with(density) { WindowInsets.navigationBars.getBottom(density).toDp() }
    return maxOf(TaskbarMetrics.floatDistance, navigationBar + TaskbarMetrics.navigationGap)
}

@Composable
fun rememberAboveTaskbarOffset(): Dp =
    rememberTaskbarOffset() + TaskbarMetrics.barHeight + TaskbarMetrics.aboveGap
