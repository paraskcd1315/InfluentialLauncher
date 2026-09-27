package com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar

import android.graphics.Bitmap
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
    onReorder: (List<AppId>) -> Unit,
    onMenu: (LauncherApp) -> Unit,
    modifier: Modifier = Modifier,
    vertical: Boolean = false
) {
    BoxWithConstraints(
        contentAlignment = if (vertical) Alignment.TopCenter else Alignment.CenterStart,
        modifier = modifier
            .then(if (vertical) Modifier.fillMaxHeight() else Modifier.fillMaxWidth())
            .infPanelSurface(RoundedCornerShape(TaskbarMetrics.barCornerRadius), blurred = LocalWindowBlurred.current)
            .then(
                if (vertical) Modifier.padding(horizontal = TaskbarMetrics.barPaddingVertical)
                else Modifier.padding(vertical = TaskbarMetrics.barPaddingVertical)
            )
    ) {
        val length = if (vertical) maxHeight else maxWidth
        val count = pinned?.size ?: TaskbarMetrics.skeletonTileCount
        val center by animateDpAsState(centerOffset(length, count), label = "taskbarCenter")
        val startEdge = center + TaskbarMetrics.barPaddingHorizontal + TaskbarMetrics.startSize
        val appsStart = startEdge + TaskbarMetrics.itemGap
        if (pinned == null) {
            PinnedAppsSkeleton(
                vertical = vertical,
                modifier = if (vertical) Modifier.padding(top = appsStart) else Modifier.padding(start = appsStart)
            )
        } else {
            PinnedApps(
                apps = pinned,
                loadIcon = loadIcon,
                onLaunch = onLaunch,
                onReorder = onReorder,
                onMenu = onMenu,
                vertical = vertical,
                contentPadding = if (vertical) {
                    PaddingValues(top = appsStart, bottom = TaskbarMetrics.barPaddingHorizontal)
                } else {
                    PaddingValues(start = appsStart, end = TaskbarMetrics.barPaddingHorizontal)
                },
                fadeInset = startEdge,
                modifier = if (vertical) Modifier.fillMaxHeight() else Modifier.fillMaxWidth()
            )
        }
        StartButton(
            open = startOpen,
            onClick = onStartClick,
            modifier = if (vertical) {
                Modifier.padding(top = center + TaskbarMetrics.barPaddingHorizontal)
            } else {
                Modifier.padding(start = center + TaskbarMetrics.barPaddingHorizontal)
            }
        )
    }
}

private fun centerOffset(length: Dp, count: Int): Dp {
    val content = TaskbarMetrics.barPaddingHorizontal * 2 + TaskbarMetrics.startSize +
        (TaskbarMetrics.pinIconSize + TaskbarMetrics.itemGap) * count
    return ((length - content) / 2).coerceAtLeast(0.dp)
}
