package com.paraskcd.influentiallauncher.startmenu.presentation.utils

import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing

object StartMenuMetrics {
    const val widthFraction = 0.9f
    val cornerRadius = DsMetrics.cornerLarge
    val windowGap = InfSpacing.s2
    val tabsHeight = DsMetrics.segmentHeight + InfSpacing.s1 * 2
    val listPadding = 16.dp
    val searchTop = 16.dp
    val searchContentGap = 8.dp
    val listTopPlain = 16.dp
    val listBottom = 16.dp
    val rowGap = 2.dp
    val rowPadding = 16.dp
    val rowIconSize = 54.dp
    val rowIconGap = 8.dp
    val pinnedColumns = 4
    val pinnedIconSize = 54.dp
    val pinnedLabelGap = 6.dp
    val pinnedCellPadding = 8.dp
    val actionGap = InfSpacing.s2
    val actionHeight = 40.dp
    val actionIconSize = 16.dp
    val actionPadding = InfSpacing.s3
    const val actionFillAlpha = 0.12f
    val permissionPadding = InfSpacing.s5
    val permissionGap = InfSpacing.s4
    val eventDotSize = 10.dp
    val dayHeaderHeight = 56.dp
    const val skeletonRows = 6
    const val skeletonAlpha = 0.10f
    val skeletonLabelHeight = InfSpacing.s4
    const val avatarAlpha = 0.22f
}
