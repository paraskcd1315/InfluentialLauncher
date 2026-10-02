// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.X
import com.paraskcd.influentiallauncher.designsystem.atoms.InfIconButton
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.designsystem.molecules.InfSettingsRow
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R

@Composable
fun SettingsRemovableRow(index: Int, count: Int, label: String, caption: String?, onRemove: () -> Unit) {
    InfGroupedCard(index = index, count = count) {
        InfSettingsRow(
            label = label,
            caption = caption,
            trailing = {
                InfIconButton(
                    icon = Lucide.X,
                    contentDescription = stringResource(R.string.startmenu_settings_remove, label),
                    tint = InfTheme.colors.textSecondary,
                    onClick = onRemove
                )
            }
        )
    }
}
