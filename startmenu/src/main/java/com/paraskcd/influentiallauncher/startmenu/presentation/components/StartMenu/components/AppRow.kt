package com.paraskcd.influentiallauncher.startmenu.presentation.components.StartMenu.components

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.designsystem.atoms.InfAsyncIcon
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.presentation.model.StartMenuApp
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppRow(
    entry: StartMenuApp,
    expanded: Boolean,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    onLaunch: (AppId, Rect?) -> Unit,
    onLongPress: () -> Unit,
    onTogglePin: () -> Unit,
    onInfo: (Rect?) -> Unit,
    onUninstall: () -> Unit,
    modifier: Modifier = Modifier
) {
    var bounds by remember { mutableStateOf<Rect?>(null) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(InfShapes.md)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(StartMenuMetrics.rowGap),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = StartMenuMetrics.rowHeight)
                .onGloballyPositioned { coordinates ->
                    val box = coordinates.boundsInWindow()
                    bounds = Rect(box.left.toInt(), box.top.toInt(), box.right.toInt(), box.bottom.toInt())
                }
                .combinedClickable(
                    onClickLabel = entry.app.label,
                    onClick = { onLaunch(entry.app.id, bounds) },
                    onLongClick = onLongPress
                )
                .padding(horizontal = StartMenuMetrics.rowPadding)
        ) {
            InfAsyncIcon(
                key = entry.app.id.key,
                size = StartMenuMetrics.rowIconSize,
                load = { loadIcon(entry.app.id, it) }
            )
            Text(
                text = entry.app.label,
                style = MaterialTheme.typography.bodyLarge,
                color = InfTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
        AnimatedVisibility(visible = expanded) {
            AppActions(
                pinned = entry.pinned,
                onTogglePin = onTogglePin,
                onInfo = { onInfo(bounds) },
                onUninstall = onUninstall
            )
        }
    }
}
