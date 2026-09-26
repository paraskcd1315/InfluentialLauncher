package com.paraskcd.influentiallauncher.clock.presentation.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing

object ClockMetrics {
    val timeSize = 72.sp
    val timeLineHeight = 76.sp
    val topGap = InfSpacing.s6
    val sideInset = InfSpacing.s6
    val dateGap = 2.dp
    const val dateAlpha = 0.85f
    const val shadowAlpha = 0.35f
    const val shadowBlur = 16f
    val shadowOffset = Offset(0f, 2f)
}
