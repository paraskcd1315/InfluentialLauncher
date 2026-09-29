// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.foundation

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.Velocity
import com.paraskcd.influentiallauncher.designsystem.theme.InfMotion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** How far a sheet has been pulled down, measured from the finger's position on the screen. */
@Stable
class SheetDrag(
    private val scope: CoroutineScope,
    private val flingVelocity: Float,
    private val onDismiss: () -> Unit
) {
    var offset by mutableFloatStateOf(0f)
        private set
    var height by mutableFloatStateOf(0f)

    private var fingerY = 0f
    private var downY = 0f
    private var anchor: Float? = null
    private val tracker = VelocityTracker()
    private var settling: Job? = null

    val swipe = SwipeUp(onDrag = { follow(from = downY) }, onEnd = { release() })

    val connection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (anchor == null || source != NestedScrollSource.UserInput) return Offset.Zero
            follow()
            return if (offset > 0f) Offset(0f, available.y) else Offset.Zero
        }

        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            if (source != NestedScrollSource.UserInput || available.y <= 0f) return Offset.Zero
            follow()
            return Offset(0f, available.y)
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            if (anchor == null) return Velocity.Zero
            release()
            return available
        }
    }

    fun onFinger(timeMillis: Long, screenY: Float, down: Boolean) {
        if (down) {
            tracker.resetTracking()
            downY = screenY
        }
        fingerY = screenY
        tracker.addPosition(timeMillis, Offset(0f, screenY))
    }

    fun reset() {
        settling?.cancel()
        anchor = null
        offset = 0f
    }

    private fun follow(from: Float = fingerY) {
        settling?.cancel()
        val start = anchor ?: (from - offset).also { anchor = it }
        offset = (fingerY - start).coerceAtLeast(0f)
    }

    private fun release() {
        anchor = null
        val velocityDown = tracker.calculateVelocity().y
        val closes = when {
            velocityDown >= flingVelocity -> true
            velocityDown <= -flingVelocity -> false
            else -> height > 0f && offset >= height * DsMetrics.sheetDismissFraction
        }
        if (closes) {
            onDismiss()
            return
        }
        settling = scope.launch {
            animate(offset, 0f, animationSpec = tween(InfMotion.durMorphMs, easing = InfMotion.easeIos)) { value, _ -> offset = value }
        }
    }
}
