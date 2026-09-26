package com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.designsystem.foundation.infPanelSurface
import com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar.components.AddPinButton
import com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar.components.PinnedApps
import com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar.components.PinnedAppsSkeleton
import com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar.components.StartButton
import com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar.components.StatusArea
import com.paraskcd.influentiallauncher.taskbar.presentation.model.StatusState
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics
import com.paraskcd.influentiallauncher.windowing.presentation.LocalWindowBlurred

@Composable
fun Taskbar(
    pinned: List<LauncherApp>?,
    status: StatusState?,
    startOpen: Boolean,
    pickerOpen: Boolean,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    onStartClick: () -> Unit,
    onAddPinClick: () -> Unit,
    onLaunch: (AppId, Rect?) -> Unit,
    onReorder: (List<AppId>) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .infPanelSurface(RoundedCornerShape(TaskbarMetrics.barCornerRadius), blurred = LocalWindowBlurred.current)
            .padding(TaskbarMetrics.barPadding)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(TaskbarMetrics.itemGap),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            StartButton(open = startOpen, onClick = onStartClick)
            if (pinned == null) {
                PinnedAppsSkeleton(modifier = Modifier.weight(1f, fill = false))
            } else {
                PinnedApps(
                    apps = pinned,
                    loadIcon = loadIcon,
                    onLaunch = onLaunch,
                    onReorder = onReorder,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }
            AddPinButton(open = pickerOpen, onClick = onAddPinClick)
        }
        if (status != null) {
            StatusArea(
                status = status,
                modifier = Modifier.padding(start = TaskbarMetrics.statusGap, end = TaskbarMetrics.barPadding)
            )
        }
    }
}
