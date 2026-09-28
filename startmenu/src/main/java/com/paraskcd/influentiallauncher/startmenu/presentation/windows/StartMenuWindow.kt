// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.windows

import android.view.Gravity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.designsystem.foundation.infPanelSurface
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics
import com.paraskcd.influentiallauncher.windowing.presentation.InfWindow
import com.paraskcd.influentiallauncher.windowing.presentation.LocalWindowBlurred

@Composable
fun StartMenuWindow(
    open: Boolean,
    progress: () -> Float,
    resizing: Boolean,
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (resizing) height * progress() else height)
                .graphicsLayer {
                    if (resizing) return@graphicsLayer
                    val shown = progress()
                    if (fromEnd) {
                        translationX = (1f - shown) * size.width * StartMenuMetrics.slideFraction
                    } else {
                        translationY = (1f - shown) * size.height * StartMenuMetrics.riseFraction
                    }
                    alpha = shown
                }
                .infPanelSurface(RoundedCornerShape(StartMenuMetrics.cornerRadius), blurred = LocalWindowBlurred.current)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.Top, unbounded = true)
                    .height(height),
                content = content
            )
        }
    }
}
