// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.foundation

import android.graphics.BlurMaskFilter
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp

fun Modifier.infParallaxShadow(
    tilt: State<Offset>,
    color: Color = Color.Black.copy(alpha = DsMetrics.parallaxShadowAlpha),
    blur: Dp = DsMetrics.parallaxShadowBlur,
    lift: Dp = DsMetrics.parallaxShadowLift,
    drift: Dp = DsMetrics.parallaxShadowDrift
): Modifier = drawWithCache {
    val paint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        this.color = color.toArgb()
        maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
    }
    val radius = size.minDimension / 2f
    val liftPx = lift.toPx()
    val driftPx = drift.toPx()
    onDrawBehind {
        val lean = tilt.value
        drawIntoCanvas {
            it.nativeCanvas.drawCircle(
                center.x + lean.x * driftPx,
                center.y + liftPx + lean.y * driftPx,
                radius,
                paint
            )
        }
    }
}
