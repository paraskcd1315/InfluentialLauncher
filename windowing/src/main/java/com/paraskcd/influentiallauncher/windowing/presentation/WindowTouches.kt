// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.windowing.presentation

import android.view.MotionEvent
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider

@Composable
fun WindowTouches(onTouch: (MotionEvent) -> Unit) {
    val window = (LocalView.current.parent as? DialogWindowProvider)?.window ?: return
    val touch by rememberUpdatedState(onTouch)
    DisposableEffect(window) {
        val original = window.callback
        window.callback = object : Window.Callback by original {
            override fun dispatchTouchEvent(event: MotionEvent): Boolean {
                touch(event)
                return original.dispatchTouchEvent(event)
            }
        }
        onDispose { window.callback = original }
    }
}
