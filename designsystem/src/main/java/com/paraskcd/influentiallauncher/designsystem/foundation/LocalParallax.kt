package com.paraskcd.influentiallauncher.designsystem.foundation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset

val LocalParallax = staticCompositionLocalOf<State<Offset>> { mutableStateOf(Offset.Zero) }
