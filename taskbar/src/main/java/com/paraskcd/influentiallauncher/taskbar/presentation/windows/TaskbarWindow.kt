// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.taskbar.presentation.windows

import android.graphics.Bitmap
import android.view.Gravity
import android.view.View
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LaunchOrigin
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.homescreen.presentation.drag.AppDragPayload
import com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar.Taskbar
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics
import com.paraskcd.influentiallauncher.homescreen.domain.model.AppSignals
import com.paraskcd.influentiallauncher.windowing.presentation.InfWindow
import com.paraskcd.influentiallauncher.windowing.presentation.isLandscape

@Composable
fun TaskbarWindow(
    offset: Dp,
    pinned: List<LauncherApp>?,
    startOpen: Boolean,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    onStartClick: () -> Unit,
    onLaunch: (AppId, LaunchOrigin?) -> Unit,
    onMenu: (LauncherApp) -> Unit,
    wiggling: Boolean,
    onDragApp: (View, LauncherApp) -> Unit,
    onDrop: (AppDragPayload, Int) -> Unit,
    visible: Boolean,
    alpha: Float,
    signals: AppSignals
) {
    val vertical = isLandscape()
    val density = LocalDensity.current
    val screenHeight = with(density) { LocalWindowInfo.current.containerSize.height.toDp() }
    InfWindow(
        cornerRadius = TaskbarMetrics.barCornerRadius,
        onDismissRequest = { },
        gravity = if (vertical) Gravity.END or Gravity.CENTER_VERTICAL else Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
        offsetX = if (vertical) offset else 0.dp,
        offsetY = if (vertical) 0.dp else offset,
        widthFraction = if (vertical) null else TaskbarMetrics.widthFraction,
        visible = visible,
        alpha = alpha
    ) {
        val touchedWindowView = LocalView.current
        Taskbar(
            pinned = pinned,
            startOpen = startOpen,
            loadIcon = loadIcon,
            onStartClick = onStartClick,
            onLaunch = onLaunch,
            onMenu = onMenu,
            wiggling = wiggling,
            onDragApp = { app -> onDragApp(touchedWindowView, app) },
            onDrop = onDrop,
            vertical = vertical,
            signals = signals,
            modifier = if (vertical) Modifier.height(screenHeight * TaskbarMetrics.heightFraction) else Modifier
        )
    }
}
