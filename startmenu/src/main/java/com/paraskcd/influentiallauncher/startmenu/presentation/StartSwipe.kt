// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics

/** A swipe up from the bottom bars that pulls the Start menu open under the finger. */
@Stable
class StartSwipe {
    var travel by mutableFloatStateOf(1f)
    var distance by mutableStateOf<Float?>(null)
        private set

    val active: Boolean get() = distance != null
    val progress: Float? get() = distance?.let { (it / travel).coerceIn(0f, 1f) }

    fun drag(up: Float) {
        distance = ((distance ?: 0f) + up).coerceIn(0f, travel)
    }

    fun releaseStaysOpen(velocityUp: Float, flingVelocity: Float): Boolean {
        val shown = progress ?: return false
        distance = null
        return when {
            velocityUp >= flingVelocity -> true
            velocityUp <= -flingVelocity -> false
            else -> shown >= StartMenuMetrics.swipeCommit
        }
    }
}
