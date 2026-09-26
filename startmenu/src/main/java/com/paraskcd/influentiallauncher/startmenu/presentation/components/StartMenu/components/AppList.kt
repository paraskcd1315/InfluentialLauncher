package com.paraskcd.influentiallauncher.startmenu.presentation.components.StartMenu.components

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.startmenu.presentation.model.AppSection
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.AppSections

@Composable
fun AppList(
    sections: List<AppSection>,
    state: LazyListState,
    expandedKey: String?,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    onHeaderClick: () -> Unit,
    onLaunch: (AppId, Rect?) -> Unit,
    onExpand: (String?) -> Unit,
    onTogglePin: (AppId) -> Unit,
    onInfo: (AppId, Rect?) -> Unit,
    onUninstall: (AppId) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(state = state, modifier = modifier) {
        sections.forEach { section ->
            item(key = AppSections.HeaderKeyPrefix + section.letter) {
                SectionHeader(letter = section.letter, onClick = onHeaderClick)
            }
            items(section.apps, key = { it.app.id.key }) { entry ->
                val key = entry.app.id.key
                AppRow(
                    entry = entry,
                    expanded = expandedKey == key,
                    loadIcon = loadIcon,
                    onLaunch = onLaunch,
                    onLongPress = { onExpand(if (expandedKey == key) null else key) },
                    onTogglePin = { onTogglePin(entry.app.id) },
                    onInfo = { bounds -> onInfo(entry.app.id, bounds) },
                    onUninstall = { onUninstall(entry.app.id) }
                )
            }
        }
    }
}
