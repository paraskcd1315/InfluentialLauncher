// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.sheets.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.foundation.InfGroupedCorners
import com.paraskcd.influentiallauncher.designsystem.theme.InfRadii
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.glance.presentation.components.SkeletonBlock
import com.paraskcd.influentiallauncher.glance.presentation.components.skeletonPulse
import com.paraskcd.influentiallauncher.glance.presentation.utils.WeatherSheetMetrics

@Composable
fun WeatherSheetSkeleton(modifier: Modifier = Modifier) {
    val color = InfTheme.colors.textPrimary
    val pulse = skeletonPulse()
    val alpha = { pulse }
    Column(verticalArrangement = Arrangement.spacedBy(InfSpacing.s5), modifier = modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(InfSpacing.s4),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = InfSpacing.s5)
        ) {
            SkeletonBlock(color, alpha, Modifier.size(WeatherSheetMetrics.nowIcon), CircleShape)
            SkeletonBlock(color, alpha, Modifier.size(WeatherSheetMetrics.skeletonTemperatureWidth, WeatherSheetMetrics.skeletonTemperatureHeight))
            Column(verticalArrangement = Arrangement.spacedBy(InfSpacing.s2)) {
                SkeletonBlock(color, alpha, Modifier.size(WeatherSheetMetrics.skeletonTitleWidth, WeatherSheetMetrics.skeletonLine))
                SkeletonBlock(color, alpha, Modifier.size(WeatherSheetMetrics.skeletonSubtitleWidth, WeatherSheetMetrics.skeletonLine))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(InfSpacing.s3), modifier = Modifier.padding(horizontal = InfSpacing.s5)) {
            repeat(WeatherSheetMetrics.skeletonStatCount) {
                SkeletonBlock(
                    color,
                    alpha,
                    Modifier
                        .weight(1f)
                        .height(WeatherSheetMetrics.skeletonTileHeight),
                    RoundedCornerShape(InfRadii.md)
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(InfSpacing.s2), modifier = Modifier.padding(horizontal = InfSpacing.s5)) {
            repeat(WeatherSheetMetrics.skeletonHourCount) {
                SkeletonBlock(
                    color,
                    alpha,
                    Modifier
                        .width(WeatherSheetMetrics.hourWidth)
                        .height(WeatherSheetMetrics.skeletonHourHeight),
                    RoundedCornerShape(InfRadii.sm)
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(DsMetrics.groupGap), modifier = Modifier.padding(horizontal = InfSpacing.s5)) {
            repeat(WeatherSheetMetrics.skeletonDayCount) { index ->
                SkeletonBlock(
                    color,
                    alpha,
                    Modifier
                        .fillMaxWidth()
                        .height(WeatherSheetMetrics.skeletonDayHeight),
                    InfGroupedCorners.of(index, WeatherSheetMetrics.skeletonDayCount)
                )
            }
        }
    }
}
