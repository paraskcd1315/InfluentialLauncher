package com.paraskcd.influentiallauncher.taskbar.presentation.windows

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.taskbar.presentation.components.AppPicker.AppPicker
import com.paraskcd.influentiallauncher.taskbar.presentation.model.PickerRow
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics
import com.paraskcd.influentiallauncher.windowing.presentation.InfWindow

@Composable
fun AppPickerWindow(
    open: Boolean,
    offsetY: Dp,
    rows: List<PickerRow>?,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    onToggle: (AppId) -> Unit,
    onClose: () -> Unit
) {
    InfWindow(
        cornerRadius = TaskbarMetrics.pickerCornerRadius,
        onDismissRequest = onClose,
        offsetY = offsetY,
        fillWidth = true,
        horizontalMargin = TaskbarMetrics.sideMargin,
        visible = open
    ) {
        AppPicker(rows = rows, loadIcon = loadIcon, onToggle = onToggle, onClose = onClose)
    }
}
