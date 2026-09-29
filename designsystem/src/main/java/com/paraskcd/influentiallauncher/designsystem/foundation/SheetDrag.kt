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
import androidx.compose.ui.unit.Velocity
import com.paraskcd.influentiallauncher.designsystem.theme.InfMotion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** How far a sheet has been pulled down, and whether letting go closes it. */
@Stable
class SheetDrag(
    private val scope: CoroutineScope,
    private val flingVelocity: Float,
    private val onDismiss: () -> Unit
) {
    var offset by mutableFloatStateOf(0f)
        private set
    var height by mutableFloatStateOf(0f)

    private var settling: Job? = null

    val swipe = SwipeUp(onDrag = { up -> drag(-up) }, onEnd = { velocityUp -> release(-velocityUp) })

    val connection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (offset <= 0f || source != NestedScrollSource.UserInput) return Offset.Zero
            val before = offset
            drag(available.y)
            return Offset(0f, offset - before)
        }

        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            if (source != NestedScrollSource.UserInput || available.y <= 0f) return Offset.Zero
            drag(available.y)
            return Offset(0f, available.y)
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            if (offset <= 0f) return Velocity.Zero
            release(available.y)
            return available
        }
    }

    fun reset() {
        settling?.cancel()
        offset = 0f
    }

    private fun drag(down: Float) {
        settling?.cancel()
        offset = (offset + down).coerceAtLeast(0f)
    }

    private fun release(velocityDown: Float) {
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
