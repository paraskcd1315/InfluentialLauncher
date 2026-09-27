package com.paraskcd.influentiallauncher.taskbar.presentation.windows

import android.graphics.Bitmap
import android.view.Gravity
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LaunchOrigin
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar.Taskbar
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics
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
    onReorder: (List<AppId>) -> Unit,
    onMenu: (LauncherApp) -> Unit,
    visible: Boolean,
    alpha: Float
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
        Taskbar(
            pinned = pinned,
            startOpen = startOpen,
            loadIcon = loadIcon,
            onStartClick = onStartClick,
            onLaunch = onLaunch,
            onReorder = onReorder,
            onMenu = onMenu,
            vertical = vertical,
            modifier = if (vertical) Modifier.height(screenHeight * TaskbarMetrics.heightFraction) else Modifier
        )
    }
}
