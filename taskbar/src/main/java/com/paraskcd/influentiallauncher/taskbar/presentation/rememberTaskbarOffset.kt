package com.paraskcd.influentiallauncher.taskbar.presentation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics

@Composable
fun rememberTaskbarOffset(): Dp {
    val density = LocalDensity.current
    val navigationBar = with(density) { WindowInsets.navigationBars.getBottom(density).toDp() }
    return navigationBar + TaskbarMetrics.bottomGap
}

@Composable
fun rememberAboveTaskbarOffset(): Dp =
    rememberTaskbarOffset() + TaskbarMetrics.barHeight + TaskbarMetrics.aboveGap
