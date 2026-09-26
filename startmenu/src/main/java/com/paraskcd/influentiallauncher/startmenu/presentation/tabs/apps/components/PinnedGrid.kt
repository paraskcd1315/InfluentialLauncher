package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.apps.components

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.startmenu.presentation.model.StartMenuApp
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics

@Composable
fun PinnedGrid(
    apps: List<StartMenuApp>,
    expandedKey: String?,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    onLaunch: (AppId, Rect?) -> Unit,
    onExpand: (String?) -> Unit,
    onToggleStart: (AppId) -> Unit,
    onToggleTaskbar: (AppId) -> Unit,
    onInfo: (AppId) -> Unit,
    onUninstall: (AppId) -> Unit,
    modifier: Modifier = Modifier
) {
    val expanded = apps.firstOrNull { it.app.id.key == expandedKey }
    Column(modifier = modifier.fillMaxWidth()) {
        apps.chunked(StartMenuMetrics.pinnedColumns).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { entry ->
                    val key = entry.app.id.key
                    PinnedTile(
                        entry = entry,
                        loadIcon = loadIcon,
                        onLaunch = onLaunch,
                        onLongPress = { onExpand(if (expandedKey == key) null else key) },
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(StartMenuMetrics.pinnedColumns - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
        AnimatedVisibility(visible = expanded != null) {
            if (expanded != null) {
                AppActions(
                    entry = expanded,
                    onToggleStart = { onToggleStart(expanded.app.id) },
                    onToggleTaskbar = { onToggleTaskbar(expanded.app.id) },
                    onInfo = { onInfo(expanded.app.id) },
                    onUninstall = { onUninstall(expanded.app.id) }
                )
            }
        }
    }
}
