package com.paraskcd.influentiallauncher.taskbar.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.taskbar.presentation.viewmodels.TaskbarViewModel
import com.paraskcd.influentiallauncher.taskbar.presentation.windows.AppPickerWindow
import com.paraskcd.influentiallauncher.taskbar.presentation.windows.TaskbarWindow

@Composable
fun TaskbarHost(
    startOpen: Boolean,
    onStartClick: () -> Unit,
    pickerOpen: Boolean,
    onPickerOpenChange: (Boolean) -> Unit,
    viewModel: TaskbarViewModel = hiltViewModel()
) {
    val pinned by viewModel.pinned.collectAsStateWithLifecycle()
    val pickerRows by viewModel.pickerRows.collectAsStateWithLifecycle()
    val barOffset = rememberTaskbarOffset()

    TaskbarWindow(
        offsetY = barOffset,
        pinned = pinned,
        startOpen = startOpen,
        pickerOpen = pickerOpen,
        loadIcon = viewModel::icon,
        onStartClick = onStartClick,
        onAddPinClick = { onPickerOpenChange(!pickerOpen) },
        onLaunch = { id, bounds ->
            onPickerOpenChange(false)
            viewModel.launch(id, bounds)
        },
        onReorder = viewModel::reorder
    )
    AppPickerWindow(
        open = pickerOpen,
        offsetY = rememberAboveTaskbarOffset(),
        rows = pickerRows,
        loadIcon = viewModel::icon,
        onToggle = viewModel::togglePin,
        onClose = { onPickerOpenChange(false) }
    )
}
