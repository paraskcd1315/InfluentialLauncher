package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.apps.components

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LaunchOrigin
import com.paraskcd.influentiallauncher.homescreen.domain.model.AppSignals
import com.paraskcd.influentiallauncher.startmenu.presentation.model.StartMenuApp
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics

@Composable
fun AppGrid(
    apps: List<StartMenuApp>,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    onLaunch: (AppId, LaunchOrigin?) -> Unit,
    onLongPress: (StartMenuApp) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = StartMenuMetrics.pinnedColumns,
    signals: AppSignals = AppSignals.None
) {
    Column(modifier = modifier.fillMaxWidth()) {
        apps.chunked(columns).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { entry ->
                    AppTile(
                        entry = entry,
                        loadIcon = loadIcon,
                        onLaunch = onLaunch,
                        onLongPress = { onLongPress(entry) },
                        badge = signals.badgeOf(entry.app.id),
                        openTasks = signals.openOf(entry.app.id),
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}
