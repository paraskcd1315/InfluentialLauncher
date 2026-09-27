package com.paraskcd.influentiallauncher.windowing.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset

val LocalWindowParallax = staticCompositionLocalOf<State<Offset>> { mutableStateOf(Offset.Zero) }
