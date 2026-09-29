// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.designsystem.foundation.SwipeUp

data class HeaderPlacement(
    val top: Dp,
    val widthFraction: Float,
    val fromEnd: Boolean,
    val offsetX: Dp,
    val shift: Dp = 0.dp,
    val alpha: Float = 1f,
    val swipe: SwipeUp? = null
)
