package com.paraskcd.influentiallauncher.startmenu.presentation.windows

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.paraskcd.influentiallauncher.designsystem.foundation.infPanelSurface
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics
import com.paraskcd.influentiallauncher.windowing.presentation.InfWindow
import com.paraskcd.influentiallauncher.windowing.presentation.LocalWindowBlurred

@Composable
fun StartMenuWindow(
    open: Boolean,
    offsetY: Dp,
    height: Dp,
    onClose: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    InfWindow(
        cornerRadius = StartMenuMetrics.cornerRadius,
        onDismissRequest = onClose,
        offsetY = offsetY,
        widthFraction = StartMenuMetrics.widthFraction,
        visible = open,
        focusable = true
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .infPanelSurface(RoundedCornerShape(StartMenuMetrics.cornerRadius), blurred = LocalWindowBlurred.current),
            content = content
        )
    }
}
