package com.paraskcd.influentiallauncher.windowing.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.staticCompositionLocalOf

val LocalBackdropAlpha = staticCompositionLocalOf<State<Float>> { mutableFloatStateOf(1f) }
