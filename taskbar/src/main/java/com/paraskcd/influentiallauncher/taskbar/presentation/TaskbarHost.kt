package com.paraskcd.influentiallauncher.taskbar.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.taskbar.presentation.viewmodels.TaskbarViewModel
import com.paraskcd.influentiallauncher.taskbar.presentation.windows.TaskbarWindow

@Composable
fun TaskbarHost(
    startOpen: Boolean,
    onStartClick: () -> Unit,
    onAppLaunched: () -> Unit,
    viewModel: TaskbarViewModel = hiltViewModel()
) {
    val pinned by viewModel.pinned.collectAsStateWithLifecycle()

    TaskbarWindow(
        offsetY = rememberTaskbarOffset(),
        pinned = pinned,
        startOpen = startOpen,
        loadIcon = viewModel::icon,
        onStartClick = onStartClick,
        onLaunch = { id, bounds ->
            onAppLaunched()
            viewModel.launch(id, bounds)
        },
        onReorder = viewModel::reorder
    )
}
