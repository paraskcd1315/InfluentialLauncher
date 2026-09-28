// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@Composable
fun InfRunningDots(
    count: Int,
    modifier: Modifier = Modifier,
    color: Color = InfTheme.colors.brand,
    dotSize: Dp = DsMetrics.runningDotSize,
    gap: Dp = DsMetrics.runningDotGap
) {
    Row(horizontalArrangement = Arrangement.spacedBy(gap), modifier = modifier) {
        repeat(count.coerceIn(0, DsMetrics.runningDotsMax)) {
            Box(modifier = Modifier.size(dotSize).background(color, CircleShape))
        }
    }
}
