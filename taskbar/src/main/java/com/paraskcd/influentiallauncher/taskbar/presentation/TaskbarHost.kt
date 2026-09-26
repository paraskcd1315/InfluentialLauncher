package com.paraskcd.influentiallauncher.taskbar.presentation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalDensity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics
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
    val status by viewModel.status.collectAsStateWithLifecycle()
    val pickerRows by viewModel.pickerRows.collectAsStateWithLifecycle()
    val density = LocalDensity.current
    val navigationBar = with(density) { WindowInsets.navigationBars.getBottom(density).toDp() }
    val barOffset = navigationBar + TaskbarMetrics.bottomGap
    val pickerOffset = barOffset + TaskbarMetrics.barHeight + TaskbarMetrics.pickerGap

    TaskbarWindow(
        offsetY = barOffset,
        pinned = pinned,
        status = status,
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
        offsetY = pickerOffset,
        rows = pickerRows,
        loadIcon = viewModel::icon,
        onToggle = viewModel::togglePin,
        onClose = { onPickerOpenChange(false) }
    )
}
