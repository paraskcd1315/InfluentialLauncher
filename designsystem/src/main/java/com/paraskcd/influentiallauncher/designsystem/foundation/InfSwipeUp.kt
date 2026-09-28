// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.foundation

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker

/** Upward drag distance and upward release velocity, in pixels. */
@Stable
class SwipeUp(val onDrag: (Float) -> Unit, val onEnd: (Float) -> Unit)

fun Modifier.infSwipeUp(swipe: SwipeUp?): Modifier = if (swipe == null) this else pointerInput(swipe) {
    val tracker = VelocityTracker()
    detectVerticalDragGestures(
        onDragStart = { tracker.resetTracking() },
        onDragEnd = { swipe.onEnd(-tracker.calculateVelocity().y) },
        onDragCancel = { swipe.onEnd(0f) },
        onVerticalDrag = { change, amount ->
            change.consume()
            tracker.addPosition(change.uptimeMillis, change.position)
            swipe.onDrag(-amount)
        }
    )
}
