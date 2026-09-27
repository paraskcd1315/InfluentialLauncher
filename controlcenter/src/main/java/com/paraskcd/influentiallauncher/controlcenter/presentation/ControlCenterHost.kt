package com.paraskcd.influentiallauncher.controlcenter.presentation

import android.view.Gravity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.controlcenter.presentation.components.ControlCenterPanel
import com.paraskcd.influentiallauncher.controlcenter.presentation.utils.ControlCenterMetrics
import com.paraskcd.influentiallauncher.controlcenter.presentation.viewmodels.ControlCenterViewModel
import com.paraskcd.influentiallauncher.designsystem.foundation.infPanelSurface
import com.paraskcd.influentiallauncher.designsystem.theme.InfMotion
import com.paraskcd.influentiallauncher.windowing.presentation.InfWindow
import com.paraskcd.influentiallauncher.windowing.presentation.LocalWindowBlurred

@Composable
fun ControlCenterHost(
    open: Boolean,
    offsetX: Dp,
    offsetY: Dp,
    widthFraction: Float,
    onClose: () -> Unit,
    fromTop: Boolean = false,
    viewModel: ControlCenterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val width = min(configuration.screenWidthDp.dp, configuration.screenHeightDp.dp) * widthFraction
    InfWindow(
        cornerRadius = ControlCenterMetrics.cornerRadius,
        onDismissRequest = onClose,
        gravity = if (fromTop) Gravity.TOP or Gravity.START else Gravity.BOTTOM or Gravity.END,
        offsetX = offsetX,
        offsetY = offsetY,
        visible = open,
        focusable = true
    ) {
        val progress by animateFloatAsState(
            targetValue = if (open) 1f else 0f,
            animationSpec = tween(InfMotion.durPushMs, easing = InfMotion.easeIos),
            label = "controlCenterRise"
        )
        Box(
            modifier = Modifier
                .width(width)
                .graphicsLayer {
                    val rise = (1f - progress) * size.height * ControlCenterMetrics.riseFraction
                    translationY = if (fromTop) -rise else rise
                    alpha = progress
                }
                .infPanelSurface(RoundedCornerShape(ControlCenterMetrics.cornerRadius), blurred = LocalWindowBlurred.current)
        ) {
            val current = state
            if (current != null) {
                ControlCenterPanel(state = current, viewModel = viewModel, onClose = onClose)
            } else {
                Box(modifier = Modifier.height(ControlCenterMetrics.cornerRadius * 2))
            }
        }
    }
}
