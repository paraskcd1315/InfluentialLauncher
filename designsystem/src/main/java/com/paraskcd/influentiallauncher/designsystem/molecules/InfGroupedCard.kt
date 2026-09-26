package com.paraskcd.influentiallauncher.designsystem.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.paraskcd.influentiallauncher.designsystem.foundation.InfGroupedCorners
import com.paraskcd.influentiallauncher.designsystem.theme.InfGlass
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@Composable
fun InfGroupedCard(
    index: Int,
    count: Int,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = InfTheme.colors
    val shape = InfGroupedCorners.of(index, count)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surfaceBright.copy(alpha = InfGlass.cardAlpha))
            .border(InfGlass.borderWidth, colors.outline.copy(alpha = InfGlass.outlineAlpha), shape),
        content = content
    )
}
