package com.paraskcd.influentiallauncher.ui.theme.modifiers

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

fun Modifier.drawFadingEdges(
    scrollableState: ScrollableState,
    topEdgeHeight: Dp = 64.dp,
    bottomEdgeHeight: Dp = 18.dp,
    fadeColor: Color = Color.Black,
    alwaysShowBoth: Boolean = true,
    keepTopAtStart: Boolean = true,
    keepBottomAtEnd: Boolean = true
) = then(
    Modifier
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            drawContent()

            val topH = topEdgeHeight.toPx()
            val bottomH = bottomEdgeHeight.toPx()
            if (topH < 1f && bottomH < 1f) return@drawWithContent

            val atTop = !scrollableState.canScrollBackward
            val atBottom = !scrollableState.canScrollForward
            val hasScrollableContent = !atTop || !atBottom

            var drawTop: Boolean
            var drawBottom: Boolean

            if (alwaysShowBoth) {
                drawTop = hasScrollableContent && topH >= 1f
                drawBottom = hasScrollableContent && bottomH >= 1f
            } else {
                drawTop = (( !atTop ) || keepTopAtStart) && topH >= 1f
                drawBottom = (( !atBottom ) || keepBottomAtEnd) && bottomH >= 1f
            }

            val h = size.height

            if (drawTop) {
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(Color.Transparent, fadeColor),
                        startY = 0f,
                        endY = topH
                    ),
                    blendMode = BlendMode.DstIn
                )
            }
            if (drawBottom) {
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(fadeColor, Color.Transparent),
                        startY = h - bottomH,
                        endY = h
                    ),
                    blendMode = BlendMode.DstIn
                )
            }
        }
)

fun Modifier.drawHorizontalFadingEdges(
    scrollableState: ScrollableState,
    leftEdgeWidth: Dp = 64.dp,
    rightEdgeWidth: Dp = 18.dp,
    fadeColor: Color = Color.Black,
    alwaysShowBoth: Boolean = false,
    keepLeftAtStart: Boolean = false,
    keepRightAtEnd: Boolean = false
) = then(
    Modifier
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            drawContent()

            val leftW = leftEdgeWidth.toPx()
            val rightW = rightEdgeWidth.toPx()
            if (leftW < 1f && rightW < 1f) return@drawWithContent

            val atLeft = !scrollableState.canScrollBackward
            val atRight = !scrollableState.canScrollForward
            val hasScrollableContent = !atLeft || !atRight

            var drawLeft: Boolean
            var drawRight: Boolean

            if (alwaysShowBoth) {
                drawLeft = hasScrollableContent && leftW >= 1f
                drawRight = hasScrollableContent && rightW >= 1f
            } else {
                drawLeft = (( !atLeft ) || keepLeftAtStart) && leftW >= 1f
                drawRight = (( !atRight ) || keepRightAtEnd) && rightW >= 1f
            }

            val w = size.width

            if (drawLeft) {
                drawRect(
                    brush = Brush.horizontalGradient(
                        listOf(Color.Transparent, fadeColor),
                        startX = 0f,
                        endX = leftW
                    ),
                    blendMode = BlendMode.DstIn
                )
            }
            if (drawRight) {
                drawRect(
                    brush = Brush.horizontalGradient(
                        listOf(fadeColor, Color.Transparent),
                        startX = w - leftW,
                        endX = w
                    ),
                    blendMode = BlendMode.DstIn
                )
            }
        }
)