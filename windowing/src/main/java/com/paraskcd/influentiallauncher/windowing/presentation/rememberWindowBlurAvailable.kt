// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.windowing.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.paraskcd.influentiallauncher.windowing.infrastructure.WindowBlur

@Composable
fun rememberWindowBlurAvailable(): State<Boolean> {
    val context = LocalContext.current
    val flow = remember(context) { WindowBlur.available(context) }
    return flow.collectAsState(initial = false)
}
