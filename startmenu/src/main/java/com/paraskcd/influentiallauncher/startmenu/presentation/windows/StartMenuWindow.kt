package com.paraskcd.influentiallauncher.startmenu.presentation.windows

import android.view.Gravity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.designsystem.foundation.infPanelSurface
import com.paraskcd.influentiallauncher.designsystem.theme.InfMotion
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics
import com.paraskcd.influentiallauncher.windowing.presentation.InfWindow
import com.paraskcd.influentiallauncher.windowing.presentation.LocalWindowBlurred

@Composable
fun StartMenuWindow(
    open: Boolean,
    offsetY: Dp,
    height: Dp,
    onClose: () -> Unit,
    widthFraction: Float = StartMenuMetrics.widthFraction,
    offsetX: Dp = 0.dp,
    fromEnd: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    InfWindow(
        cornerRadius = StartMenuMetrics.cornerRadius,
        onDismissRequest = onClose,
        gravity = if (fromEnd) Gravity.BOTTOM or Gravity.END else Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
        offsetX = offsetX,
        offsetY = offsetY,
        widthFraction = widthFraction,
        visible = open,
        focusable = true,
        showStatusBar = true
    ) {
        val progress by animateFloatAsState(
            targetValue = if (open) 1f else 0f,
            animationSpec = tween(InfMotion.durPushMs, easing = InfMotion.easeIos),
            label = "startMenuRise"
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .graphicsLayer {
                    if (fromEnd) {
                        translationX = (1f - progress) * size.width * StartMenuMetrics.slideFraction
                    } else {
                        translationY = (1f - progress) * size.height * StartMenuMetrics.riseFraction
                    }
                    alpha = progress
                }
                .infPanelSurface(RoundedCornerShape(StartMenuMetrics.cornerRadius), blurred = LocalWindowBlurred.current),
            content = content
        )
    }
}
