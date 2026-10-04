// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.homescreen.presentation.components

import android.graphics.Bitmap
import androidx.compose.runtime.staticCompositionLocalOf
import com.paraskcd.influentiallauncher.apps.domain.model.AppId

val LocalIconLayers = staticCompositionLocalOf<(suspend (AppId, Int) -> Pair<Bitmap, Bitmap>?)?> { null }
