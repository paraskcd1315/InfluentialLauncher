package com.paraskcd.influentiallauncher.taskbar.presentation.utils

import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfRadii
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing

object TaskbarMetrics {
    val barCornerRadius = InfRadii.xl
    val sideMargin = InfSpacing.s3
    val bottomGap = InfSpacing.s2
    val barPadding = InfSpacing.s2
    val barHeight = DsMetrics.tileSize + barPadding * 2
    val itemGap = InfSpacing.s1
    val startGlyphSize = 24.dp
    const val skeletonTileCount = 4
    val aboveGap = InfSpacing.s2
    const val skeletonAlpha = 0.10f
    const val draggingScale = 1.12f
}
