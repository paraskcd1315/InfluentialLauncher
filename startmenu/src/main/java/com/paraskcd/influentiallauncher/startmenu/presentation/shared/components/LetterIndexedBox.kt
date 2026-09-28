// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.shared.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paraskcd.influentiallauncher.designsystem.molecules.InfLetterScrubber

@Composable
fun LetterIndexedBox(
    letters: List<Char>,
    available: Set<Char>,
    scrubberPadding: PaddingValues,
    onJump: (Char) -> Unit,
    onScrub: (Char?) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        content()
        InfLetterScrubber(
            letters = letters,
            available = available,
            onScrub = { letter ->
                onScrub(letter)
                if (letter != null && letter in available) onJump(letter)
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(scrubberPadding)
        )
    }
}
