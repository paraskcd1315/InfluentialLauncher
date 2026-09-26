package com.paraskcd.influentiallauncher.presentation

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.core.util.Consumer
import com.paraskcd.influentiallauncher.clock.presentation.ClockHeader
import com.paraskcd.influentiallauncher.statusbar.presentation.StatusBarHost
import com.paraskcd.influentiallauncher.taskbar.presentation.TaskbarHost
import com.paraskcd.influentiallauncher.taskbar.presentation.TaskbarLayout
import com.paraskcd.influentiallauncher.taskbar.presentation.rememberAboveTaskbarOffset

@Composable
fun Desktop(activity: ComponentActivity) {
    var pickerOpen by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(activity) {
        val listener = Consumer<Intent> { pickerOpen = false }
        activity.addOnNewIntentListener(listener)
        onDispose { activity.removeOnNewIntentListener(listener) }
    }
    BackHandler(enabled = pickerOpen) { pickerOpen = false }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures { pickerOpen = false } }
    ) {
        ClockHeader(modifier = Modifier.align(Alignment.TopStart))
    }
    TaskbarHost(
        startOpen = false,
        onStartClick = { },
        pickerOpen = pickerOpen,
        onPickerOpenChange = { pickerOpen = it }
    )
    StatusBarHost(
        offsetX = TaskbarLayout.sideMargin,
        offsetY = rememberAboveTaskbarOffset(),
        visible = !pickerOpen
    )
}
