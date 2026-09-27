package com.paraskcd.influentiallauncher.startmenu.presentation.windows

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.paraskcd.influentiallauncher.designsystem.atoms.InfSearchField
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
    onClose: () -> Unit
) {
    InfWindow(
        cornerRadius = InfRadii.pill,
        onDismissRequest = onClose,
        offsetY = offsetY,
        widthFraction = widthFraction,
        visible = visible,
        focusable = true,
        liftAboveIme = false,
        showStatusBar = true
    ) {
        InfSearchField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            clearDescription = clearDescription
        )
    }
}
