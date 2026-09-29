// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.windows

import android.view.Gravity
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.paraskcd.influentiallauncher.designsystem.foundation.infSwipeUp
import com.paraskcd.influentiallauncher.designsystem.theme.InfRadii
import com.paraskcd.influentiallauncher.startmenu.presentation.model.HeaderPlacement
import com.paraskcd.influentiallauncher.windowing.presentation.InfWindow

@Composable
fun HeaderWindow(
    visible: Boolean,
    placement: HeaderPlacement,
    offsetTop: Dp,
    onHeight: (Dp) -> Unit,
    onClose: () -> Unit,
    cornerRadius: Dp = InfRadii.pill,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    InfWindow(
        cornerRadius = cornerRadius,
        onDismissRequest = onClose,
        gravity = Gravity.TOP or if (placement.fromEnd) Gravity.END else Gravity.CENTER_HORIZONTAL,
        offsetX = placement.offsetX,
        offsetY = placement.top + offsetTop + placement.shift,
        widthFraction = placement.widthFraction,
        visible = visible,
        alpha = placement.alpha
    ) {
        Box(
            modifier = Modifier
                .onSizeChanged { onHeight(with(density) { it.height.toDp() }) }
                .infSwipeUp(placement.swipe)
        ) {
            content()
        }
    }
}
