// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.taskbar.presentation

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.homescreen.presentation.drag.AppDrag
import com.paraskcd.influentiallauncher.homescreen.presentation.drag.AppDragPayload
import com.paraskcd.influentiallauncher.homescreen.presentation.drag.DragSource
import com.paraskcd.influentiallauncher.pins.domain.model.PinTarget
import com.paraskcd.influentiallauncher.taskbar.presentation.sheets.TaskbarAppSheet
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics
import com.paraskcd.influentiallauncher.taskbar.presentation.viewmodels.TaskbarViewModel
import com.paraskcd.influentiallauncher.taskbar.presentation.windows.TaskbarWindow

@Composable
fun TaskbarHost(
    startOpen: Boolean,
    onStartClick: () -> Unit,
    onAppLaunched: () -> Unit,
    visible: Boolean = true,
    alpha: Float = 1f,
    viewModel: TaskbarViewModel = hiltViewModel()
) {
    val pinned by viewModel.pinned.collectAsStateWithLifecycle()
    val startPins by viewModel.startPins.collectAsStateWithLifecycle()
    val wiggling by viewModel.wiggling.collectAsStateWithLifecycle()
    val signals by viewModel.signals.collectAsStateWithLifecycle()
    val tint = InfTheme.colors.brandText.toArgb()
    val iconBackground = InfTheme.colors.glassStrongBg.toArgb()
    val pinPx = with(LocalDensity.current) { TaskbarMetrics.pinIconSize.roundToPx() }
    val loadIcon: suspend (AppId, Int) -> Bitmap? = remember(tint, iconBackground) { { id, px -> viewModel.icon(id, px, tint, iconBackground) } }
    var menuApp by remember { mutableStateOf<LauncherApp?>(null) }

    TaskbarWindow(
        offset = rememberTaskbarOffset(),
        pinned = pinned,
        startOpen = startOpen,
        loadIcon = loadIcon,
        onStartClick = onStartClick,
        onLaunch = { id, bounds ->
            onAppLaunched()
            viewModel.launch(id, bounds)
        },
        onMenu = { menuApp = it },
        wiggling = wiggling,
        onDragApp = { view, app ->
            AppDrag.start(view, AppDragPayload(DragSource.Taskbar, app), viewModel.cachedIcon(app.id, pinPx, tint, iconBackground), pinPx)
        },
        onDrop = viewModel::drop,
        visible = visible,
        alpha = alpha,
        signals = signals
    )
    TaskbarAppSheet(
        app = menuApp,
        onStart = menuApp?.id in startPins,
        loadIcon = loadIcon,
        onDismiss = { menuApp = null },
        onEdit = { viewModel.startEdit() },
        onUnpin = { viewModel.togglePin(PinTarget.Taskbar, it) },
        onToggleStart = { viewModel.togglePin(PinTarget.Start, it) },
        onInfo = {
            onAppLaunched()
            viewModel.openInfo(it)
        },
        onUninstall = {
            onAppLaunched()
            viewModel.uninstall(it)
        },
        onClose = viewModel::closeApp
    )
}
