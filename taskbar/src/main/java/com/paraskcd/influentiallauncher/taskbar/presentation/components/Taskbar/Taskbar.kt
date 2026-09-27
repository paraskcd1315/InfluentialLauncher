package com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    modifier: Modifier = Modifier
) {
    val startEdge = TaskbarMetrics.barPaddingHorizontal + TaskbarMetrics.startSize
    val appsStart = startEdge + TaskbarMetrics.itemGap
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .fillMaxWidth()
            .infPanelSurface(RoundedCornerShape(TaskbarMetrics.barCornerRadius), blurred = LocalWindowBlurred.current)
            .padding(vertical = TaskbarMetrics.barPaddingVertical)
    ) {
        if (pinned == null) {
            PinnedAppsSkeleton(modifier = Modifier.padding(start = appsStart))
        } else {
            PinnedApps(
                apps = pinned,
                loadIcon = loadIcon,
                onLaunch = onLaunch,
                onReorder = onReorder,
                onMenu = onMenu,
                contentPadding = PaddingValues(start = appsStart, end = TaskbarMetrics.barPaddingHorizontal),
                fadeStartInset = startEdge,
                modifier = Modifier.fillMaxWidth()
            )
        }
        StartButton(open = startOpen, onClick = onStartClick, modifier = Modifier.padding(start = TaskbarMetrics.barPaddingHorizontal))
    }
}
