// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.windows

import android.view.Gravity
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import com.paraskcd.influentiallauncher.designsystem.atoms.InfSearchField
import com.paraskcd.influentiallauncher.designsystem.foundation.SwipeUp
import com.paraskcd.influentiallauncher.designsystem.foundation.infSwipeUp
import com.paraskcd.influentiallauncher.designsystem.theme.InfRadii
import com.paraskcd.influentiallauncher.windowing.presentation.InfWindow

@Composable
fun StartSearchWindow(
    visible: Boolean,
    offsetY: Dp,
    widthFraction: Float,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    clearDescription: String,
    onClose: () -> Unit,
    fromEnd: Boolean = false,
    offsetX: Dp = 0.dp,
    alpha: Float = 1f,
    swipe: SwipeUp? = null
) {
    InfWindow(
        cornerRadius = InfRadii.pill,
        onDismissRequest = onClose,
        gravity = if (fromEnd) Gravity.BOTTOM or Gravity.END else Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
        offsetX = offsetX,
        offsetY = offsetY,
        widthFraction = widthFraction,
        visible = visible,
        focusable = true,
        liftAboveIme = false,
        showStatusBar = true,
        alpha = alpha
    ) {
        Box(modifier = Modifier.infSwipeUp(swipe)) {
            InfSearchField(
                value = value,
                onValueChange = onValueChange,
                placeholder = placeholder,
                clearDescription = clearDescription
            )
        }
    }
}
