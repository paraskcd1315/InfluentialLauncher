package com.paraskcd.influentiallauncher.glance.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.glance.presentation.utils.GlanceMetrics

@Composable
fun skeletonPulse(): Float {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val pulse by transition.animateFloat(
        initialValue = GlanceMetrics.skeletonMinAlpha,
        targetValue = GlanceMetrics.skeletonMaxAlpha,
        animationSpec = infiniteRepeatable(tween(GlanceMetrics.skeletonPulseMs), RepeatMode.Reverse),
        label = "skeletonAlpha"
    )
    return pulse
}

@Composable
fun SkeletonBlock(color: Color, alpha: () -> Float, modifier: Modifier = Modifier, shape: Shape = InfShapes.pill) {
    Box(
        modifier = modifier
            .graphicsLayer { this.alpha = alpha() }
            .background(color, shape)
    )
}
