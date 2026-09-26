package com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar.components

import android.graphics.Bitmap
import android.graphics.Rect
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
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.designsystem.atoms.InfAsyncIcon
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics

@Composable
fun PinnedAppTile(
    app: LauncherApp,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    onLaunch: (AppId, Rect?) -> Unit,
    modifier: Modifier = Modifier
) {
    var bounds by remember { mutableStateOf<Rect?>(null) }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(TaskbarMetrics.pinCornerRadius))
            .onGloballyPositioned { coordinates ->
                val box = coordinates.boundsInWindow()
                bounds = Rect(box.left.toInt(), box.top.toInt(), box.right.toInt(), box.bottom.toInt())
            }
            .clickable(onClickLabel = app.label) { onLaunch(app.id, bounds) }
    ) {
        InfAsyncIcon(key = app.id.key, size = TaskbarMetrics.pinIconSize, load = { loadIcon(app.id, it) }, version = loadIcon)
    }
}
