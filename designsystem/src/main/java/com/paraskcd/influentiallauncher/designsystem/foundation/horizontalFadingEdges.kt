package com.paraskcd.influentiallauncher.designsystem.foundation

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.horizontalFadingEdges(
    state: ScrollableState,
    edgeWidth: Dp = DsMetrics.fadeEdge,
    startInset: Dp = 0.dp,
    endInset: Dp = 0.dp
): Modifier = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        val width = edgeWidth.toPx()
        val start = startInset.toPx()
        val end = size.width - endInset.toPx()
        if (state.canScrollBackward) {
            drawRect(
                brush = Brush.horizontalGradient(listOf(Color.Transparent, Color.Black), startX = start, endX = start + width),
                blendMode = BlendMode.DstIn
            )
        }
        if (state.canScrollForward) {
            drawRect(
                brush = Brush.horizontalGradient(listOf(Color.Black, Color.Transparent), startX = end - width, endX = end),
                blendMode = BlendMode.DstIn
            )
        }
    }
