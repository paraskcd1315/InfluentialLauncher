package com.paraskcd.influentiallauncher.windowing.presentation

import android.view.Gravity
import android.view.ViewGroup
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.paraskcd.influentiallauncher.designsystem.foundation.LocalInfBlurred
import com.paraskcd.influentiallauncher.designsystem.foundation.LocalParallax
import com.paraskcd.influentiallauncher.designsystem.theme.InfMotion
import com.paraskcd.influentiallauncher.windowing.infrastructure.DialogWindowSetup
import kotlinx.coroutines.android.awaitFrame
import kotlin.math.roundToInt

@Composable
fun InfWindow(
    cornerRadius: Dp,
    onDismissRequest: () -> Unit,
    gravity: Int = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    fillWidth: Boolean = false,
    horizontalMargin: Dp = 0.dp,
    widthFraction: Float? = null,
    visible: Boolean = true,
    focusable: Boolean = false,
    fullScreen: Boolean = false,
    liftAboveIme: Boolean = true,
    showStatusBar: Boolean = false,
    alpha: Float = 1f,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val parallax = LocalParallax.current
    val imeBottom = WindowInsets.ime.getBottom(density)
    val requestedOffsetPx = with(density) { offsetY.roundToPx() }
    val imeOffsetPx = with(density) { imeBottom + WindowMetrics.ImeGap.roundToPx() }
    val offsetYPx = if (focusable && liftAboveIme && !fullScreen && imeBottom > 0) maxOf(requestedOffsetPx, imeOffsetPx) else requestedOffsetPx

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
            dismissOnClickOutside = false,
            dismissOnBackPress = focusable
        )
    ) {
        val window = (LocalView.current.parent as DialogWindowProvider).window
        val screenWidthDp = LocalConfiguration.current.screenWidthDp
        val cornerRadiusPx = with(density) { cornerRadius.toPx() }
        val offsetXPx = with(density) { offsetX.roundToPx() }
        val marginPx = with(density) { horizontalMargin.roundToPx() }
        val elevationPx = with(density) { WindowMetrics.Elevation.toPx() }
        val blurAvailable by rememberWindowBlurAvailable()
        val blur = remember { Animatable(0f) }
        val reveal = remember { Animatable(0f) }
        var configured by remember { mutableStateOf(false) }

        remember(cornerRadiusPx, offsetXPx, gravity, fillWidth, marginPx, widthFraction, screenWidthDp, fullScreen) {
            val displayWidth = DialogWindowSetup.displayWidth(window)
            val width = when {
                fullScreen -> displayWidth
                widthFraction != null -> (displayWidth * widthFraction).toInt()
                fillWidth -> displayWidth - marginPx * 2
                else -> ViewGroup.LayoutParams.WRAP_CONTENT
            }
            DialogWindowSetup.configure(
                window = window,
                widthPx = width,
                heightPx = if (fullScreen) DialogWindowSetup.displayHeight(window) else ViewGroup.LayoutParams.WRAP_CONTENT,
                gravity = gravity,
                offsetXPx = offsetXPx,
                offsetYPx = offsetYPx,
                cornerRadiusPx = cornerRadiusPx,
                elevationPx = if (fullScreen) 0f else elevationPx,
                shadowAlpha = WindowMetrics.ShadowAlpha,
                fullScreen = fullScreen
            )
            DialogWindowSetup.place(window, offsetXPx, offsetYPx, alpha = 0f)
        }
        LaunchedEffect(Unit) {
            awaitFrame()
            awaitFrame()
            configured = true
        }
        LaunchedEffect(visible, configured) {
            if (!configured) return@LaunchedEffect
            reveal.animateTo(if (visible) 1f else 0f, tween(InfMotion.durMorphMs, easing = InfMotion.easeIos))
        }
        LaunchedEffect(visible, showStatusBar, focusable) {
            if (focusable) DialogWindowSetup.setStatusBar(window, visible = visible && showStatusBar)
        }
        val shown = configured && (visible || reveal.value > 0f)
        val progress = reveal.value * alpha.coerceIn(0f, 1f)
        val blurRadius = if (shown) (blur.value * progress).toInt() else 0
        val appliedBlur = remember { intArrayOf(-1) }
        SideEffect {
            if (appliedBlur[0] != blurRadius) {
                appliedBlur[0] = blurRadius
                DialogWindowSetup.setBlur(window, blurRadius)
            }
            DialogWindowSetup.setVisible(window, shown, focusable = focusable && visible)
        }
        val baseX = rememberUpdatedState(offsetXPx)
        val baseY = rememberUpdatedState(offsetYPx)
        val shownAlpha = rememberUpdatedState(progress)
        val drift = if (fullScreen) 0f else with(density) { WindowMetrics.ParallaxShift.toPx() }
        val xSign = if (gravity and Gravity.HORIZONTAL_GRAVITY_MASK == Gravity.RIGHT) -1f else 1f
        val ySign = if (gravity and Gravity.VERTICAL_GRAVITY_MASK == Gravity.BOTTOM) -1f else 1f
        LaunchedEffect(window, drift, xSign, ySign) {
            snapshotFlow {
                val tilt = parallax.value
                WindowPlacement(
                    x = baseX.value - (tilt.x * drift * xSign).roundToInt(),
                    y = baseY.value - (tilt.y * drift * ySign).roundToInt(),
                    alpha = shownAlpha.value
                )
            }.collect { DialogWindowSetup.place(window, it.x, it.y, it.alpha) }
        }
        LaunchedEffect(blurAvailable, shown) {
            if (!shown) {
                blur.snapTo(0f)
                return@LaunchedEffect
            }
            val target = if (blurAvailable) WindowMetrics.BlurRadiusMax.toFloat() else 0f
            blur.animateTo(target, tween(WindowMetrics.BlurRampMs))
        }
        CompositionLocalProvider(
            LocalWindowBlurred provides blurAvailable,
            LocalInfBlurred provides blurAvailable,
            content = content
        )
    }
}
