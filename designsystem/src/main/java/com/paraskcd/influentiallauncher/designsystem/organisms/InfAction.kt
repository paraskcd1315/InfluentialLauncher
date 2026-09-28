// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.organisms

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class InfAction(
    val icon: ImageVector,
    val label: String,
    val tint: Color,
    val run: () -> Unit
)
