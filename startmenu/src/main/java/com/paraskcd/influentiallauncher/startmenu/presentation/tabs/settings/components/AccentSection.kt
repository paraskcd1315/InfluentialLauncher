// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.designsystem.atoms.InfButton
import com.paraskcd.influentiallauncher.designsystem.atoms.InfSectionHeader
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.AccentPalette

@Composable
fun AccentSection(
    onPick: (String) -> Unit,
    onWallpaper: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(InfSpacing.s3),
        modifier = modifier.fillMaxWidth()
    ) {
        InfSectionHeader(text = stringResource(R.string.startmenu_accent_section))
        Row(horizontalArrangement = Arrangement.spacedBy(InfSpacing.s3)) {
            AccentPalette.seeds.forEach { seed ->
                Row(
                    modifier = Modifier
                        .size(SwatchSize)
                        .clip(CircleShape)
                        .background(AccentPalette.color(seed))
                        .clickable { onPick(seed) }
                ) {}
            }
        }
        InfButton(
            label = stringResource(R.string.startmenu_accent_wallpaper),
            onClick = onWallpaper,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private val SwatchSize = 36.dp
