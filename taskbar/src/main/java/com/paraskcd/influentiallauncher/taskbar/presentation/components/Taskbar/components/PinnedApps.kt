package com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar.components

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LaunchOrigin
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.designsystem.foundation.horizontalFadingEdges
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun PinnedApps(
    apps: List<LauncherApp>,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    onLaunch: (AppId, LaunchOrigin?) -> Unit,
    onReorder: (List<AppId>) -> Unit,
    onMenu: (LauncherApp) -> Unit,
    modifier: Modifier = Modifier
) {
    var order by remember { mutableStateOf(apps) }
    var moved by remember { mutableStateOf(false) }
    var held by remember { mutableStateOf(false) }
    LaunchedEffect(apps) { order = apps }

    val listState = rememberLazyListState()
    val reorderState = rememberReorderableLazyListState(listState) { from, to ->
        val fromIndex = order.indexOfFirst { it.id.key == from.key }
        val toIndex = order.indexOfFirst { it.id.key == to.key }
        if (fromIndex < 0 || toIndex < 0) return@rememberReorderableLazyListState
        moved = true
        order = order.toMutableList().apply { add(toIndex, removeAt(fromIndex)) }
    }
    val draggingFill = InfTheme.colors.glassStrongBg

    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(TaskbarMetrics.itemGap),
        modifier = modifier.horizontalFadingEdges(listState)
    ) {
        items(order, key = { it.id.key }) { app ->
            ReorderableItem(reorderState, key = app.id.key) { dragging ->
                val scale by animateFloatAsState(if (dragging) TaskbarMetrics.draggingScale else 1f, label = "pinScale")
                PinnedAppTile(
                    app = app,
                    loadIcon = loadIcon,
                    onLaunch = { id, bounds -> if (held) held = false else onLaunch(id, bounds) },
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .background(if (dragging) draggingFill else Color.Transparent, RoundedCornerShape(TaskbarMetrics.pinCornerRadius))
                        .longPressDraggableHandle(
                            onDragStarted = {
                                moved = false
                                held = true
                            },
                            onDragStopped = {
                                if (moved) {
                                    held = false
                                    onReorder(order.map { it.id })
                                } else {
                                    onMenu(app)
                                }
                            }
                        )
                )
            }
        }
    }
}
