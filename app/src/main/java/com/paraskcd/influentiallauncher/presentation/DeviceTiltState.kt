package com.paraskcd.influentiallauncher.presentation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.paraskcd.influentiallauncher.devicestatus.domain.ports.DeviceTiltSource

@Composable
fun rememberDeviceTilt(activity: ComponentActivity, source: DeviceTiltSource): State<Offset> {
    val tilt = remember { mutableStateOf(Offset.Zero) }
    LaunchedEffect(activity, source) {
        activity.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            try {
                source.tilt.collect { tilt.value = Offset(it.x, it.y) }
            } finally {
                tilt.value = Offset.Zero
            }
        }
    }
    return tilt
}
