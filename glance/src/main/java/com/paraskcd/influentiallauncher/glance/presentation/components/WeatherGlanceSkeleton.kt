package com.paraskcd.influentiallauncher.glance.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.paraskcd.influentiallauncher.designsystem.theme.LocalWallpaperInk
import com.paraskcd.influentiallauncher.glance.presentation.utils.GlanceMetrics

@Composable
fun WeatherGlanceSkeleton(modifier: Modifier = Modifier) {
    val pulse = skeletonPulse()
    Row(
        horizontalArrangement = Arrangement.spacedBy(GlanceMetrics.itemGap),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        SkeletonBlock(LocalWallpaperInk.current.content, { pulse }, Modifier.size(GlanceMetrics.icon), CircleShape)
        SkeletonBlock(LocalWallpaperInk.current.content, { pulse }, Modifier.size(GlanceMetrics.skeletonTemperature, GlanceMetrics.skeletonTemperatureHeight))
        Column(verticalArrangement = Arrangement.spacedBy(GlanceMetrics.skeletonLineGap)) {
            SkeletonBlock(LocalWallpaperInk.current.content, { pulse }, Modifier.size(GlanceMetrics.skeletonTitleWidth, GlanceMetrics.skeletonLine))
            SkeletonBlock(LocalWallpaperInk.current.content, { pulse }, Modifier.size(GlanceMetrics.skeletonSubtitleWidth, GlanceMetrics.skeletonLine))
        }
    }
}
