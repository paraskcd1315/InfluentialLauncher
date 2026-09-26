package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.apps.components

import android.graphics.Bitmap
import android.graphics.Rect
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
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    onLaunch: (AppId, Rect?) -> Unit,
    onLongPress: (StartMenuApp) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        apps.chunked(StartMenuMetrics.pinnedColumns).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { entry ->
                    PinnedTile(
                        entry = entry,
                        loadIcon = loadIcon,
                        onLaunch = onLaunch,
                        onLongPress = { onLongPress(entry) },
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(StartMenuMetrics.pinnedColumns - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}
