package com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics

@Composable
fun PinnedAppsSkeleton(modifier: Modifier = Modifier, vertical: Boolean = false) {
    val fill = InfTheme.colors.textPrimary.copy(alpha = TaskbarMetrics.skeletonAlpha)
    val tiles: @Composable () -> Unit = {
        repeat(TaskbarMetrics.skeletonTileCount) {
            Box(
                modifier = Modifier
                    .size(TaskbarMetrics.pinIconSize)
                    .clip(CircleShape)
                    .background(fill)
            )
        }
    }
    if (vertical) {
        Column(verticalArrangement = Arrangement.spacedBy(TaskbarMetrics.itemGap), modifier = modifier) { tiles() }
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(TaskbarMetrics.itemGap), modifier = modifier) { tiles() }
    }
}
