package com.paraskcd.influentiallauncher.taskbar.presentation.components.AppPicker.components

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
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics

@Composable
fun AppPickerSkeleton(modifier: Modifier = Modifier) {
    val fill = InfTheme.colors.textPrimary.copy(alpha = TaskbarMetrics.skeletonAlpha)
    Column(modifier = modifier) {
        repeat(TaskbarMetrics.skeletonTileCount) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(InfSpacing.s3),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(TaskbarMetrics.pickerRowHeight)
                    .padding(horizontal = InfSpacing.s3)
            ) {
                Box(Modifier.size(TaskbarMetrics.pickerIconSize).clip(CircleShape).background(fill))
                Box(Modifier.weight(1f).height(InfSpacing.s4).clip(InfShapes.sm).background(fill))
            }
        }
    }
}
