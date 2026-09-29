// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.presentation.utils

object SearchPeekRelease {
    fun opens(progress: Float, velocityUp: Float, flingVelocity: Float): Boolean = when {
        velocityUp >= flingVelocity -> true
        velocityUp <= -flingVelocity -> false
        else -> progress >= DesktopMetrics.searchCommit
    }
}
