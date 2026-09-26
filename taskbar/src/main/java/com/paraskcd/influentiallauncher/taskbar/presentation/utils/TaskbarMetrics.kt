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
    val statusIconSize = 18.dp
    val statusGap = InfSpacing.s2
    const val skeletonTileCount = 4
    val pickerCornerRadius = InfRadii.xl
    val pickerGap = InfSpacing.s2
    val pickerMaxHeight = 420.dp
    val pickerPadding = InfSpacing.s3
    val pickerRowHeight = 56.dp
    val pickerIconSize = 36.dp
    val pickerCheckSize = 20.dp
    const val skeletonAlpha = 0.10f
    const val draggingScale = 1.12f
}
