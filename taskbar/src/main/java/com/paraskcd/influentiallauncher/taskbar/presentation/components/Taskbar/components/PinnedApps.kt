package com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar.components

import android.graphics.Bitmap
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LaunchOrigin
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.designsystem.foundation.horizontalFadingEdges
import com.paraskcd.influentiallauncher.designsystem.foundation.verticalFadingEdges
import com.paraskcd.influentiallauncher.homescreen.presentation.drag.AppDrag
import com.paraskcd.influentiallauncher.homescreen.presentation.drag.AppDragPayload
import com.paraskcd.influentiallauncher.homescreen.presentation.drag.DragSource
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics

@Composable
fun PinnedApps(
    apps: List<LauncherApp>,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    wiggling: Boolean,
    onLaunch: (AppId, LaunchOrigin?) -> Unit,
    onMenu: (LauncherApp) -> Unit,
    onDragApp: (LauncherApp) -> Unit,
    onDrop: (AppDragPayload, Int) -> Unit,
    modifier: Modifier = Modifier,
    vertical: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(),
    fadeInset: Dp = 0.dp
) {
    val listState = rememberLazyListState()
    val drag = remember { TaskbarDragState() }
    var origin by remember { mutableStateOf(Offset.Zero) }
    val latestApps by rememberUpdatedState(apps)
    val latestDrop by rememberUpdatedState(onDrop)
    val wiggle = wiggleAngle(wiggling)
    val target = remember {
        TaskbarDropTarget(
            drag = drag,
            origin = { origin },
            vertical = vertical,
            listState = listState,
            apps = { latestApps },
            onDrop = { payload, index -> latestDrop(payload, index) }
        )
    }
    val slots = slotsFor(apps, drag)

    val tiles: LazyListScope.() -> Unit = {
        itemsIndexed(slots, key = { index, app -> app?.id?.key ?: PlaceholderKey + index }) { index, app ->
            if (app == null) {
                Spacer(modifier = Modifier.size(TaskbarMetrics.pinIconSize))
            } else {
                PinnedAppTile(
                    app = app,
                    loadIcon = loadIcon,
                    wiggling = wiggling,
                    wiggle = if (index % 2 == 0) wiggle else -wiggle,
                    onLaunch = onLaunch,
                    onMenu = { onMenu(app) },
                    onDrag = { onDragApp(app) }
                )
            }
        }
    }

    val listModifier = modifier
        .onGloballyPositioned { origin = it.positionInWindow() }
        .dragAndDropTarget(shouldStartDragAndDrop = { AppDrag.payloadOf(it.toAndroidDragEvent()) != null }, target = target)

    if (vertical) {
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(TaskbarMetrics.itemGap),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = contentPadding,
            userScrollEnabled = !drag.dragging,
            modifier = listModifier.verticalFadingEdges(listState, topInset = fadeInset),
            content = tiles
        )
    } else {
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(TaskbarMetrics.itemGap),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = contentPadding,
            userScrollEnabled = !drag.dragging,
            modifier = listModifier.horizontalFadingEdges(listState, startInset = fadeInset),
            content = tiles
        )
    }
}

@Stable
private class TaskbarDragState {
    var payload by mutableStateOf<AppDragPayload?>(null)
    var hoverIndex by mutableIntStateOf(-1)
    val dragging: Boolean get() = payload != null
}

private class TaskbarDropTarget(
    private val drag: TaskbarDragState,
    private val origin: () -> Offset,
    private val vertical: Boolean,
    private val listState: LazyListState,
    private val apps: () -> List<LauncherApp>,
    private val onDrop: (AppDragPayload, Int) -> Unit
) : DragAndDropTarget {
    override fun onStarted(event: DragAndDropEvent) {
        drag.payload = AppDrag.payloadOf(event.toAndroidDragEvent())
        drag.hoverIndex = -1
    }

    override fun onMoved(event: DragAndDropEvent) {
        val android = event.toAndroidDragEvent()
        val local = Offset(android.x, android.y) - origin()
        val position = if (vertical) local.y else local.x
        val dragged = drag.payload?.app?.id
        val real = listState.layoutInfo.visibleItemsInfo.filter { (it.key as? String)?.startsWith(PlaceholderKey) != true }
        val before = real.count { it.offset + it.size / 2 < position }
        val firstVisible = apps().indexOfFirst { app -> app.id.key == real.firstOrNull()?.key }.coerceAtLeast(0)
        val others = apps().count { it.id != dragged }
        drag.hoverIndex = (firstVisible + before).coerceIn(0, others)
    }

    override fun onExited(event: DragAndDropEvent) {
        drag.hoverIndex = -1
    }

    override fun onDrop(event: DragAndDropEvent): Boolean {
        val payload = AppDrag.payloadOf(event.toAndroidDragEvent()) ?: return false
        onDrop(payload, drag.hoverIndex.coerceAtLeast(0))
        return true
    }

    override fun onEnded(event: DragAndDropEvent) {
        drag.payload = null
        drag.hoverIndex = -1
    }
}

private fun slotsFor(apps: List<LauncherApp>, drag: TaskbarDragState): List<LauncherApp?> {
    val payload = drag.payload ?: return apps
    val base: List<LauncherApp?> = if (payload.source == DragSource.Taskbar) apps.filterNot { it.id == payload.app.id } else apps
    if (drag.hoverIndex < 0) return base
    return base.toMutableList().apply { add(drag.hoverIndex.coerceIn(0, size), null) }
}

@Composable
private fun wiggleAngle(active: Boolean): Float {
    if (!active) return 0f
    val transition = rememberInfiniteTransition(label = "taskbarWiggle")
    val angle by transition.animateFloat(
        initialValue = -TaskbarMetrics.wiggleDegrees,
        targetValue = TaskbarMetrics.wiggleDegrees,
        animationSpec = infiniteRepeatable(tween(TaskbarMetrics.wiggleMs), RepeatMode.Reverse),
        label = "taskbarWiggleAngle"
    )
    return angle
}

private const val PlaceholderKey = "placeholder:"
