package com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar.components

import android.graphics.Bitmap
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LaunchOrigin
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.apps.infrastructure.LaunchOrigins
import com.paraskcd.influentiallauncher.designsystem.atoms.InfAsyncIcon
import com.paraskcd.influentiallauncher.homescreen.presentation.components.IconSignals
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics

@Composable
fun PinnedAppTile(
    app: LauncherApp,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    wiggling: Boolean,
    wiggle: Float,
    onLaunch: (AppId, LaunchOrigin?) -> Unit,
    onMenu: () -> Unit,
    onDrag: () -> Unit,
    modifier: Modifier = Modifier,
    badge: Int = 0,
    openTasks: Int = 0
) {
    val view = LocalView.current
    var iconBounds by remember { mutableStateOf<Rect?>(null) }
    val editing by rememberUpdatedState(wiggling)
    val launch = { onLaunch(app.id, iconBounds?.let { LaunchOrigins.scaleUp(view, it.toAndroidRectF()) }) }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .graphicsLayer { rotationZ = wiggle }
            .semantics { onClick(label = app.label) { launch(); true } }
            .pointerInput(app.id) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val outcome = withTimeoutOrNull(viewConfiguration.longPressTimeoutMillis) {
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: return@withTimeoutOrNull TileOutcome.Cancel
                            if (change.changedToUpIgnoreConsumed()) {
                                return@withTimeoutOrNull if (change.isConsumed) TileOutcome.Cancel else TileOutcome.Tap
                            }
                            val moved = (change.position - down.position).getDistance() > viewConfiguration.touchSlop
                            if (moved && editing) return@withTimeoutOrNull TileOutcome.Drag
                            if (change.isConsumed || moved) return@withTimeoutOrNull TileOutcome.Cancel
                        }
                        @Suppress("UNREACHABLE_CODE")
                        TileOutcome.Cancel
                    } ?: TileOutcome.LongPress
                    when (outcome) {
                        TileOutcome.Tap -> if (!editing) launch()
                        TileOutcome.Drag -> onDrag()
                        TileOutcome.LongPress -> if (editing) onDrag() else onMenu()
                        TileOutcome.Cancel -> Unit
                    }
                }
            }
    ) {
        InfAsyncIcon(
            key = app.id.key,
            size = TaskbarMetrics.pinIconSize,
            load = { loadIcon(app.id, it) },
            version = loadIcon,
            modifier = Modifier.onGloballyPositioned { iconBounds = it.boundsInWindow() }
        )
        IconSignals(badge = badge, openTasks = openTasks, dotsDrop = TaskbarMetrics.signalDotsDrop)
    }
}

private enum class TileOutcome { Tap, Drag, LongPress, Cancel }
