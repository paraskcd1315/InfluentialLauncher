// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics

/** A swipe that pulls the Start menu open from the bottom bars, or pushes it closed from inside. */
@Stable
class StartSwipe {
    var travel by mutableFloatStateOf(1f)
    var distance by mutableStateOf<Float?>(null)
        private set
    var closing by mutableStateOf(false)
        private set

    val active: Boolean get() = distance != null
    val progress: Float? get() = distance?.let { (it / travel).coerceIn(0f, 1f) }

    fun drag(up: Float) {
        if (distance == null) closing = false
        distance = ((distance ?: 0f) + up).coerceIn(0f, travel)
    }

    fun dragClose(up: Float) {
        if (distance == null) closing = true
        distance = ((distance ?: travel) + up).coerceIn(0f, travel)
    }

    fun back(progress: Float) {
        closing = true
        distance = travel * (1f - progress.coerceIn(0f, 1f))
    }

    fun endBack() {
        distance = null
        closing = false
    }

    fun releaseStaysOpen(velocityUp: Float, flingVelocity: Float): Boolean {
        val shown = progress ?: return false
        val commit = if (closing) 1f - StartMenuMetrics.swipeCommit else StartMenuMetrics.swipeCommit
        distance = null
        closing = false
        return when {
            velocityUp >= flingVelocity -> true
            velocityUp <= -flingVelocity -> false
            else -> shown >= commit
        }
    }
}
