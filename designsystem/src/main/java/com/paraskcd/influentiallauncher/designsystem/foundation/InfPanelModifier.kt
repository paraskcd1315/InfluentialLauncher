package com.paraskcd.influentiallauncher.designsystem.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import com.paraskcd.influentiallauncher.designsystem.theme.InfGlass
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@Composable
fun Modifier.infPanelSurface(shape: Shape, blurred: Boolean): Modifier {
    val colors = InfTheme.colors
    val alpha = if (blurred) InfGlass.panelAlphaBlurred else InfGlass.panelAlphaSolid
    return this
        .clip(shape)
        .background(colors.bgBase.copy(alpha = alpha))
        .border(InfGlass.borderWidth, colors.glassBorder, shape)
        .infSpecularEdge(shape)
}
