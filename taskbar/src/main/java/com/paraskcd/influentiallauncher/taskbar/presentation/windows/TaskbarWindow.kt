package com.paraskcd.influentiallauncher.taskbar.presentation.windows

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar.Taskbar
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics
import com.paraskcd.influentiallauncher.windowing.presentation.InfWindow

@Composable
fun TaskbarWindow(
    offsetY: Dp,
    pinned: List<LauncherApp>?,
    startOpen: Boolean,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    onStartClick: () -> Unit,
    onLaunch: (AppId, Rect?) -> Unit,
    onReorder: (List<AppId>) -> Unit,
    onMenu: (LauncherApp) -> Unit
) {
    InfWindow(
        cornerRadius = TaskbarMetrics.barCornerRadius,
        onDismissRequest = { },
        offsetY = offsetY,
        widthFraction = TaskbarMetrics.widthFraction
    ) {
        Taskbar(
            pinned = pinned,
            startOpen = startOpen,
            loadIcon = loadIcon,
            onStartClick = onStartClick,
            onLaunch = onLaunch,
            onReorder = onReorder,
            onMenu = onMenu
        )
    }
}
