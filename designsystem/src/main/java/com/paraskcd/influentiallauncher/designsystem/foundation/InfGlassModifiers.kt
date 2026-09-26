package com.paraskcd.influentiallauncher.designsystem.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.paraskcd.influentiallauncher.designsystem.theme.InfGlass
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@Composable
fun Modifier.infGlassSurface(
    shape: Shape,
    specular: Boolean = true,
    strong: Boolean = false,
    panel: Boolean = false
): Modifier {
    val colors = InfTheme.colors
    val fill = when {
        panel -> colors.bgBase.copy(alpha = InfGlass.panelAlphaBlurred)
        strong -> colors.glassStrongBg
        else -> colors.glassBg
    }
    return this
        .clip(shape)
        .background(fill)
        .border(InfGlass.borderWidth, colors.glassBorder, shape)
        .then(if (specular) Modifier.infSpecularEdge(shape) else Modifier)
}

@Composable
fun Modifier.infSpecularEdge(shape: Shape): Modifier {
    val colors = InfTheme.colors
    return border(
        width = InfGlass.specularWidth,
        brush = Brush.verticalGradient(
            0f to colors.glassSpecular,
            InfGlass.specularStop to Color.Transparent,
            1f to Color.Transparent
        ),
        shape = shape
    )
}
