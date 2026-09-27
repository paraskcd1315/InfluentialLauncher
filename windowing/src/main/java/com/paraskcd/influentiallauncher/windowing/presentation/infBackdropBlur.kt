package com.paraskcd.influentiallauncher.windowing.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import com.paraskcd.influentiallauncher.windowing.infrastructure.BackdropBlur
import kotlin.math.roundToInt

@Composable
fun Modifier.infBackdropBlur(
    blurRadius: Dp = WindowMetrics.IconBlurRadius,
    tint: Color = Color.Transparent
): Modifier {
    val view = LocalView.current
    val alpha = LocalBackdropAlpha.current
    val radiusPx = with(LocalDensity.current) { blurRadius.toPx().roundToInt() }
    val drawable = remember(view, radiusPx, tint) { BackdropBlur.create(view, radiusPx, tint.toArgb()) }
        ?: return this
    return drawBehind {
        drawable.setBounds(0, 0, size.width.roundToInt(), size.height.roundToInt())
        BackdropBlur.setCornerRadius(drawable, size.minDimension / 2f)
        drawable.alpha = (alpha.value.coerceIn(0f, 1f) * 255).roundToInt()
        drawIntoCanvas { drawable.draw(it.nativeCanvas) }
    }
}
