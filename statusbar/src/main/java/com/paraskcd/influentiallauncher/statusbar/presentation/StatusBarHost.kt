// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.statusbar.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.view.Gravity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.core.content.ContextCompat
import com.paraskcd.influentiallauncher.designsystem.foundation.SwipeUp
import com.paraskcd.influentiallauncher.designsystem.foundation.infSwipeUp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.statusbar.presentation.components.StatusPill
import com.paraskcd.influentiallauncher.statusbar.presentation.utils.StatusBarMetrics
import com.paraskcd.influentiallauncher.statusbar.presentation.viewmodels.StatusBarViewModel
import com.paraskcd.influentiallauncher.windowing.presentation.InfWindow

@Composable
fun StatusBarHost(
    offsetX: Dp,
    offsetY: Dp,
    visible: Boolean,
    alpha: Float = 1f,
    active: Boolean = false,
    onClick: () -> Unit = {},
    fromTop: Boolean = false,
    swipeUp: SwipeUp? = null,
    viewModel: StatusBarViewModel = hiltViewModel()
) {
    val status by viewModel.status.collectAsStateWithLifecycle()
    val current = status
    val context = LocalContext.current
    val phoneState = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { viewModel.refresh() }
    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
        if (granted != PackageManager.PERMISSION_GRANTED) phoneState.launch(Manifest.permission.READ_PHONE_STATE)
    }
    LifecycleResumeEffect(Unit) {
        viewModel.refresh()
        onPauseOrDispose { }
    }
    InfWindow(
        cornerRadius = StatusBarMetrics.cornerRadius,
        onDismissRequest = { },
        gravity = if (fromTop) Gravity.TOP or Gravity.START else Gravity.BOTTOM or Gravity.END,
        offsetX = offsetX,
        offsetY = offsetY,
        visible = visible && current != null,
        alpha = alpha
    ) {
        if (current != null) StatusPill(status = current, active = active, onClick = onClick, modifier = Modifier.infSwipeUp(swipeUp))
    }
}
