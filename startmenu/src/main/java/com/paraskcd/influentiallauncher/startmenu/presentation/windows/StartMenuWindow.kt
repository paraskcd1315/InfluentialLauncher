package com.paraskcd.influentiallauncher.startmenu.presentation.windows

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics
import com.paraskcd.influentiallauncher.windowing.presentation.InfWindow

@Composable
fun StartMenuWindow(
    open: Boolean,
    offsetY: Dp,
    horizontalMargin: Dp,
    onClose: () -> Unit,
    content: @Composable () -> Unit
) {
    InfWindow(
        cornerRadius = StartMenuMetrics.cornerRadius,
        onDismissRequest = onClose,
        offsetY = offsetY,
        fillWidth = true,
        horizontalMargin = horizontalMargin,
        visible = open,
        focusable = true,
        content = content
    )
}
