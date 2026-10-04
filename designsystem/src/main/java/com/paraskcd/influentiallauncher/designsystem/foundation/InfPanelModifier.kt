// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur
import com.paraskcd.influentiallauncher.designsystem.theme.InfGlass
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@Composable
fun Modifier.infPanelSurface(shape: Shape, blurred: Boolean): Modifier {
    LocalInfHaze.current?.let { return infHazeSurface(shape, it) }
    val alpha = if (blurred) InfGlass.panelAlphaBlurred else InfGlass.panelAlphaSolid
    return this
        .clip(shape)
        .background(panelBase().copy(alpha = alpha))
        .infPanelEdge(shape)
}

@Composable
fun Modifier.infHazeSurface(shape: Shape, state: HazeState, alpha: Float = 1f): Modifier {
    val tint = panelBase().copy(alpha = InfGlass.panelAlphaBlurred)
    return this
        .clip(shape)
        .hazeBlur(
            input = HazeInput.Backdrop(state),
            style = HazeBlurStyle {
                backgroundColor(Color.Transparent)
                colorEffects(listOf(HazeColorEffect.tint(tint)))
                blurRadius(InfGlass.hazeBlur)
                noiseFactor(0f)
                alpha(alpha)
            }
        )
        .infPanelEdge(shape)
}

@Composable
private fun panelBase(): Color {
    val colors = InfTheme.colors
    return if (LocalPanelTint.current) lerp(colors.bgBase, colors.brand, InfGlass.panelTint) else colors.bgBase
}

@Composable
private fun Modifier.infPanelEdge(shape: Shape): Modifier =
    this.border(InfGlass.borderWidth, InfTheme.colors.glassBorder, shape).infSpecularEdge(shape)
