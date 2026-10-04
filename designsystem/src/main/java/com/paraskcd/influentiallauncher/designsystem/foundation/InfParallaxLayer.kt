// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.foundation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity

@Composable
fun Modifier.infParallaxLayer(): Modifier {
    if (!LocalParallaxLayers.current) return this
    val tilt = LocalParallax.current
    val step = with(LocalDensity.current) { DsMetrics.parallaxLayerStep.toPx() }
    return graphicsLayer {
        val lean = tilt.value
        translationX = -lean.x * step
        translationY = -lean.y * step
    }
}
