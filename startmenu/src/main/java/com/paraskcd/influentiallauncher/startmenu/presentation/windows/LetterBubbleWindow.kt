// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.windows

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import com.paraskcd.influentiallauncher.designsystem.atoms.InfLetterBubble
import com.paraskcd.influentiallauncher.designsystem.theme.InfRadii
import com.paraskcd.influentiallauncher.windowing.presentation.InfWindow
import com.paraskcd.influentiallauncher.windowing.presentation.LocalWindowBlurred

@Composable
fun LetterBubbleWindow(
    letter: Char?,
    offsetY: Dp
) {
    var shown by remember { mutableStateOf(letter ?: 'A') }
    if (letter != null) shown = letter
    InfWindow(
        cornerRadius = InfRadii.md,
        onDismissRequest = {},
        offsetY = offsetY,
        visible = letter != null
    ) {
        InfLetterBubble(letter = shown, blurred = LocalWindowBlurred.current)
    }
}
