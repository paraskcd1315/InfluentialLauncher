package com.paraskcd.influentiallauncher.startmenu.presentation.components.StartMenu.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.AppSections
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics

@Composable
fun AlphabetGrid(
    available: Set<Char>,
    onPick: (Char) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(columns = GridCells.Fixed(StartMenuMetrics.alphabetColumns), modifier = modifier) {
        items(AppSections.Letters, key = { it.toString() }) { letter ->
            val enabled = letter in available
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(StartMenuMetrics.alphabetCellHeight)
                    .clip(InfShapes.md)
                    .clickable(enabled = enabled) { onPick(letter) }
                    .alpha(if (enabled) 1f else StartMenuMetrics.disabledAlpha)
            ) {
                Text(
                    text = letter.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = if (enabled) InfTheme.colors.brandText else InfTheme.colors.textTertiary
                )
            }
        }
    }
}
