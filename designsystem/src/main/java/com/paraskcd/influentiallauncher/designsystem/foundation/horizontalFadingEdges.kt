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

fun Modifier.horizontalFadingEdges(
    state: ScrollableState,
    edgeWidth: Dp = DsMetrics.fadeEdge
): Modifier = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        val width = edgeWidth.toPx()
        if (state.canScrollBackward) {
            drawRect(
                brush = Brush.horizontalGradient(listOf(Color.Transparent, Color.Black), startX = 0f, endX = width),
                blendMode = BlendMode.DstIn
            )
        }
        if (state.canScrollForward) {
            drawRect(
                brush = Brush.horizontalGradient(
                    listOf(Color.Black, Color.Transparent),
                    startX = size.width - width,
                    endX = size.width
                ),
                blendMode = BlendMode.DstIn
            )
        }
    }
