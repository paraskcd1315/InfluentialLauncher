package com.paraskcd.influentiallauncher.designsystem.foundation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

fun Modifier.infGradientTint(brush: Brush): Modifier = this
    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.SrcIn)
    }

@Composable
fun Modifier.infAccentGradientTint(): Modifier {
    val ramp = InfTheme.ramp
    return infGradientTint(Brush.linearGradient(listOf(ramp.s400, ramp.s700)))
}
