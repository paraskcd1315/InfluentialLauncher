// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar

import android.graphics.Bitmap
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LaunchOrigin
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.designsystem.foundation.infPanelSurface
import com.paraskcd.influentiallauncher.designsystem.foundation.infParallaxLayer
import com.paraskcd.influentiallauncher.homescreen.domain.model.AppSignals
import com.paraskcd.influentiallauncher.homescreen.presentation.drag.AppDragPayload
import com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar.components.PinnedApps
import com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar.components.PinnedAppsSkeleton
import com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar.components.StartButton
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics
import com.paraskcd.influentiallauncher.windowing.presentation.LocalWindowBlurred

@Composable
fun Taskbar(
    pinned: List<LauncherApp>?,
    startOpen: Boolean,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    onStartClick: () -> Unit,
    onLaunch: (AppId, LaunchOrigin?) -> Unit,
    onMenu: (LauncherApp) -> Unit,
    wiggling: Boolean,
    onDragApp: (LauncherApp) -> Unit,
    onDrop: (AppDragPayload, Int) -> Unit,
    modifier: Modifier = Modifier,
    vertical: Boolean = false,
    signals: AppSignals = AppSignals.None,
    showStart: Boolean = true
) {
    BoxWithConstraints(
        contentAlignment = if (vertical) Alignment.TopCenter else Alignment.CenterStart,
        modifier = modifier
            .then(if (vertical) Modifier.fillMaxHeight().width(TaskbarMetrics.barHeight) else Modifier.fillMaxWidth().height(TaskbarMetrics.barHeight))
            .infPanelSurface(RoundedCornerShape(TaskbarMetrics.barCornerRadius), blurred = LocalWindowBlurred.current)
    ) {
        val length = if (vertical) maxHeight else maxWidth
        val count = pinned?.size ?: TaskbarMetrics.skeletonTileCount
        val center by animateDpAsState(centerOffset(length, count, showStart), label = "taskbarCenter")
        val startEdge = center + TaskbarMetrics.barPaddingHorizontal + if (showStart) TaskbarMetrics.startSize else 0.dp
        val appsStart = startEdge + if (showStart) TaskbarMetrics.itemGap else 0.dp
        if (pinned == null) {
            PinnedAppsSkeleton(
                vertical = vertical,
                modifier = if (vertical) Modifier.padding(top = appsStart) else Modifier.padding(start = appsStart)
            )
        } else {
            PinnedApps(
                apps = pinned,
                loadIcon = loadIcon,
                wiggling = wiggling,
                onLaunch = onLaunch,
                onMenu = onMenu,
                onDragApp = onDragApp,
                onDrop = onDrop,
                vertical = vertical,
                signals = signals,
                contentPadding = if (vertical) {
                    PaddingValues(top = appsStart, bottom = TaskbarMetrics.barPaddingHorizontal, start = TaskbarMetrics.barPaddingVertical, end = TaskbarMetrics.barPaddingVertical)
                } else {
                    PaddingValues(start = appsStart, end = TaskbarMetrics.barPaddingHorizontal, top = TaskbarMetrics.barPaddingVertical, bottom = TaskbarMetrics.barPaddingVertical)
                },
                fadeInset = startEdge,
                modifier = Modifier.infParallaxLayer().fillMaxSize()
            )
        }
        if (showStart) {
            StartButton(
                open = startOpen,
                onClick = onStartClick,
                modifier = if (vertical) {
                    Modifier.padding(top = center + TaskbarMetrics.barPaddingHorizontal).infParallaxLayer()
                } else {
                    Modifier.padding(start = center + TaskbarMetrics.barPaddingHorizontal).infParallaxLayer()
                }
            )
        }
    }
}

private fun centerOffset(length: Dp, count: Int, showStart: Boolean): Dp {
    val start = if (showStart) TaskbarMetrics.startSize else 0.dp
    val content = TaskbarMetrics.barPaddingHorizontal * 2 + start +
        (TaskbarMetrics.pinIconSize + TaskbarMetrics.itemGap) * count
    return ((length - content) / 2).coerceAtLeast(0.dp)
}
