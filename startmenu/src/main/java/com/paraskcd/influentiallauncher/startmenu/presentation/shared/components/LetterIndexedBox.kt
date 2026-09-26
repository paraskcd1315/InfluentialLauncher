package com.paraskcd.influentiallauncher.startmenu.presentation.shared.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paraskcd.influentiallauncher.designsystem.atoms.InfLetterBubble
import com.paraskcd.influentiallauncher.designsystem.molecules.InfLetterScrubber
import com.paraskcd.influentiallauncher.windowing.presentation.LocalWindowBlurred

@Composable
fun LetterIndexedBox(
    letters: List<Char>,
    available: Set<Char>,
    scrubberPadding: PaddingValues,
    onJump: (Char) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    var active by remember { mutableStateOf<Char?>(null) }
    Box(modifier = modifier.fillMaxSize()) {
        content()
        InfLetterScrubber(
            letters = letters,
            available = available,
            onScrub = { letter ->
                active = letter
                if (letter != null && letter in available) onJump(letter)
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(scrubberPadding)
        )
        active?.let { letter ->
            InfLetterBubble(letter = letter, blurred = LocalWindowBlurred.current, modifier = Modifier.align(Alignment.Center))
        }
    }
}
