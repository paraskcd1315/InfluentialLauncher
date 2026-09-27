package com.paraskcd.influentiallauncher.statusbar.presentation

import android.view.Gravity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.Dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
    viewModel: StatusBarViewModel = hiltViewModel()
) {
    val status by viewModel.status.collectAsStateWithLifecycle()
    val current = status
    InfWindow(
        cornerRadius = StatusBarMetrics.cornerRadius,
        onDismissRequest = { },
        gravity = Gravity.BOTTOM or Gravity.END,
        offsetX = offsetX,
        offsetY = offsetY,
        visible = visible && current != null,
        alpha = alpha
    ) {
        if (current != null) StatusPill(status = current)
    }
}
