package com.paraskcd.influentiallauncher.taskbar.presentation.utils

import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing

object TaskbarMetrics {
    const val widthFraction = 0.8f
    val barCornerRadius = DsMetrics.cornerLarge
    val floatDistance = 48.dp
    val navigationGap = InfSpacing.s2
    val barPaddingVertical = 24.dp
    val barPaddingHorizontal = 8.dp
    val startSize = 56.dp
    val startCornerRadius = 16.dp
    val startGlyphSize = 32.dp
    val barHeight = startSize + barPaddingVertical * 2
    val itemGap = 8.dp
    val pinIconSize = 54.dp
    val pinCornerRadius = 14.dp
    val aboveGap = InfSpacing.s2
    const val skeletonTileCount = 4
    const val skeletonAlpha = 0.10f
    const val draggingScale = 1.12f
}
