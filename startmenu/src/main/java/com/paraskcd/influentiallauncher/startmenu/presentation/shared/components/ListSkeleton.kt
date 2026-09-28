// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics

@Composable
fun ListSkeleton(modifier: Modifier = Modifier) {
    val fill = InfTheme.colors.textPrimary.copy(alpha = StartMenuMetrics.skeletonAlpha)
    Column(verticalArrangement = Arrangement.spacedBy(StartMenuMetrics.rowGap), modifier = modifier) {
        repeat(StartMenuMetrics.skeletonRows) { index ->
            InfGroupedCard(index = index, count = StartMenuMetrics.skeletonRows) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(StartMenuMetrics.rowIconGap),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(StartMenuMetrics.rowPadding)
                ) {
                    Box(Modifier.size(StartMenuMetrics.rowIconSize).clip(CircleShape).background(fill))
                    Box(Modifier.weight(1f).height(StartMenuMetrics.skeletonLabelHeight).clip(InfShapes.sm).background(fill))
                }
            }
        }
    }
}
