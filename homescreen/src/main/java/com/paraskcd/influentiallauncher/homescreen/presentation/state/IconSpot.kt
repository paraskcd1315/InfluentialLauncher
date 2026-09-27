package com.paraskcd.influentiallauncher.homescreen.presentation.state

import androidx.compose.ui.geometry.Rect
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp

data class IconSpot(val app: LauncherApp, val bounds: Rect)
