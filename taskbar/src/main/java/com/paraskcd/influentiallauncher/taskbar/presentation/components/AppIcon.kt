package com.paraskcd.influentiallauncher.taskbar.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics

@Composable
fun AppIcon(
    id: AppId,
    size: Dp,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    modifier: Modifier = Modifier
) {
    val sizePx = with(LocalDensity.current) { size.roundToPx() }
    val icon by produceState<ImageBitmap?>(initialValue = null, id, sizePx) {
        value = loadIcon(id, sizePx)?.asImageBitmap()
    }
    val bitmap = icon
    if (bitmap == null) {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(InfTheme.colors.textPrimary.copy(alpha = TaskbarMetrics.skeletonAlpha))
        )
    } else {
        Image(bitmap = bitmap, contentDescription = null, modifier = modifier.size(size))
    }
}
