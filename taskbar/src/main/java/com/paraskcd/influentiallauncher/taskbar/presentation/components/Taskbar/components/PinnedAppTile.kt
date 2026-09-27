package com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar.components

import android.graphics.Bitmap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LaunchOrigin
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.apps.infrastructure.LaunchOrigins
import com.paraskcd.influentiallauncher.designsystem.atoms.InfAsyncIcon
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics

@Composable
fun PinnedAppTile(
    app: LauncherApp,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    onLaunch: (AppId, LaunchOrigin?) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var iconBounds by remember { mutableStateOf<Rect?>(null) }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(TaskbarMetrics.pinCornerRadius))
            .clickable(onClickLabel = app.label) { onLaunch(app.id, iconBounds?.let { LaunchOrigins.scaleUp(view, it.toAndroidRectF()) }) }
    ) {
        InfAsyncIcon(
            key = app.id.key,
            size = TaskbarMetrics.pinIconSize,
            load = { loadIcon(app.id, it) },
            version = loadIcon,
            modifier = Modifier.onGloballyPositioned { iconBounds = it.boundsInWindow() }
        )
    }
}
