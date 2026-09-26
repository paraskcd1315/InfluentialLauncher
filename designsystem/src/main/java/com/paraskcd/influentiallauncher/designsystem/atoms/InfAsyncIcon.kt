package com.paraskcd.influentiallauncher.designsystem.atoms

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
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@Composable
fun InfAsyncIcon(
    key: String,
    size: Dp,
    load: suspend (Int) -> Bitmap?,
    modifier: Modifier = Modifier
) {
    val sizePx = with(LocalDensity.current) { size.roundToPx() }
    val icon by produceState<ImageBitmap?>(initialValue = null, key, sizePx) {
        value = load(sizePx)?.asImageBitmap()
    }
    val bitmap = icon
    if (bitmap == null) {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(InfTheme.colors.textPrimary.copy(alpha = DsMetrics.skeletonAlpha))
        )
    } else {
        Image(bitmap = bitmap, contentDescription = null, modifier = modifier.size(size))
    }
}
