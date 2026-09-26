package com.paraskcd.influentiallauncher.taskbar.presentation

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.pins.domain.model.PinTarget
import com.paraskcd.influentiallauncher.taskbar.presentation.sheets.TaskbarAppSheet
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
    val startPins by viewModel.startPins.collectAsStateWithLifecycle()
    val tint = InfTheme.colors.brandText.toArgb()
    val loadIcon: suspend (AppId, Int) -> Bitmap? = remember(tint) { { id, px -> viewModel.icon(id, px, tint) } }
    var menuApp by remember { mutableStateOf<LauncherApp?>(null) }

    TaskbarWindow(
        offsetY = rememberTaskbarOffset(),
        pinned = pinned,
        startOpen = startOpen,
        loadIcon = loadIcon,
        onStartClick = onStartClick,
        onLaunch = { id, bounds ->
            onAppLaunched()
            viewModel.launch(id, bounds)
        },
        onReorder = viewModel::reorder,
        onMenu = { menuApp = it }
    )
    TaskbarAppSheet(
        app = menuApp,
        onStart = menuApp?.id in startPins,
        loadIcon = loadIcon,
        onDismiss = { menuApp = null },
        onUnpin = { viewModel.togglePin(PinTarget.Taskbar, it) },
        onToggleStart = { viewModel.togglePin(PinTarget.Start, it) },
        onInfo = {
            onAppLaunched()
            viewModel.openInfo(it)
        },
        onUninstall = {
            onAppLaunched()
            viewModel.uninstall(it)
        }
    )
}
