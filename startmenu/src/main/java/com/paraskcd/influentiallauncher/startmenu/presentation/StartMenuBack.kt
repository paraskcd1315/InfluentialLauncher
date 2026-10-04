// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CancellationException

/** The back gesture shrinks the Start menu like the closing swipe; every focusable Start window carries one. */
@Composable
fun StartMenuBack(open: Boolean, swipe: StartSwipe, onClose: () -> Unit) {
    PredictiveBackHandler(enabled = open) { events ->
        try {
            events.collect { swipe.back(it.progress) }
            swipe.endBack()
            onClose()
        } catch (cancelled: CancellationException) {
            swipe.endBack()
            throw cancelled
        }
    }
}
