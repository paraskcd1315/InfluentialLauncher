package com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar.components

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.designsystem.atoms.InfTile
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.taskbar.presentation.components.AppIcon

@Composable
fun PinnedAppTile(
    app: LauncherApp,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    onLaunch: (AppId, Rect?) -> Unit,
    modifier: Modifier = Modifier
) {
    var bounds by remember { mutableStateOf<Rect?>(null) }
    InfTile(
        onClick = { onLaunch(app.id, bounds) },
        contentDescription = app.label,
        modifier = modifier.onGloballyPositioned { coordinates ->
            val box = coordinates.boundsInWindow()
            bounds = Rect(box.left.toInt(), box.top.toInt(), box.right.toInt(), box.bottom.toInt())
        }
    ) {
        AppIcon(id = app.id, size = DsMetrics.tileIconSize, loadIcon = loadIcon)
    }
}
