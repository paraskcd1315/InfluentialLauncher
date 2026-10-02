// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.statusbar.presentation.utils

import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.designsystem.theme.InfRadii
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing

object StatusBarMetrics {
    val cornerRadius = InfRadii.pill
    val minHeight = 48.dp
    val paddingHorizontal = InfSpacing.s4
    val paddingVertical = 10.dp
    val iconSize = 18.dp
    val iconGap = InfSpacing.s2
    const val stackedColumns = 4
    const val stackedStroke = 1.5f / 18f
    const val stackedInset = 1.5f / 18f
    const val stackedPitch = 3.375f / 18f
    const val stackedWidth = stackedInset * 2 + stackedStroke + stackedPitch * (stackedColumns - 1)
    const val stackedBarBottom = 13f / 18f
    const val stackedBarShortest = 3f / 18f
    const val stackedBarTallest = 10.5f / 18f
    const val stackedDotCentre = 15.75f / 18f
    const val stackedUnlitAlpha = 0.35f
}
