package com.paraskcd.influentiallauncher.startmenu.presentation.components.StartMenu.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics

@Composable
fun SectionHeader(
    letter: Char,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val jumpLabel = stringResource(R.string.startmenu_jump)
    Text(
        text = letter.toString(),
        style = MaterialTheme.typography.titleMedium,
        color = InfTheme.colors.brandText,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = StartMenuMetrics.headerHeight)
            .clip(InfShapes.sm)
            .clickable(onClickLabel = jumpLabel, onClick = onClick)
            .padding(horizontal = StartMenuMetrics.headerPadding)
            .wrapContentHeight()
    )
}
