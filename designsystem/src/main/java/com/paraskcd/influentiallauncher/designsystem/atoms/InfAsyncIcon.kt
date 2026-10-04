// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

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
import androidx.compose.ui.graphics.graphicsLayer
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.foundation.LocalParallax
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@Composable
fun InfAsyncIcon(
    key: String,
    size: Dp,
    load: suspend (Int) -> Bitmap?,
    modifier: Modifier = Modifier,
    version: Any? = null,
    layers: (suspend (Int) -> Pair<Bitmap, Bitmap>?)? = null
) {
    val density = LocalDensity.current
    val sizePx = with(density) { size.roundToPx() }
    val loaded by produceState<Pair<ImageBitmap, ImageBitmap?>?>(initialValue = null, key, sizePx, version, layers != null) {
        value = layers?.invoke(sizePx)?.let { (plate, glyph) -> plate.asImageBitmap() to glyph.asImageBitmap() }
            ?: load(sizePx)?.let { it.asImageBitmap() to null }
    }
    val icon = loaded
    val bitmap = icon?.first
    val glyph = icon?.second
    if (bitmap != null && glyph != null) {
        val tilt = LocalParallax.current
        val step = with(density) { DsMetrics.parallaxLayerStep.toPx() }
        Box(modifier = modifier.size(size).clip(CircleShape)) {
            Image(bitmap = bitmap, contentDescription = null, modifier = Modifier.size(size))
            Image(
                bitmap = glyph,
                contentDescription = null,
                modifier = Modifier
                    .size(size)
                    .graphicsLayer {
                        val lean = tilt.value
                        translationX = -lean.x * step
                        translationY = -lean.y * step
                    }
            )
        }
    } else if (bitmap == null) {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(InfTheme.colors.textPrimary.copy(alpha = DsMetrics.skeletonAlpha))
        )
    } else {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = modifier
                .size(size)
                .clip(CircleShape)
        )
    }
}
