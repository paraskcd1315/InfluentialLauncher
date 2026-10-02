// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.designsystem.molecules.InfSettingsRow
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@Composable
fun SettingsActionRow(index: Int, count: Int, label: String, onClick: () -> Unit) {
    InfGroupedCard(index = index, count = count) {
        InfSettingsRow(
            label = label,
            modifier = Modifier.clickable(onClick = onClick),
            trailing = {
                Icon(
                    imageVector = Lucide.Plus,
                    contentDescription = null,
                    tint = InfTheme.colors.textSecondary,
                    modifier = Modifier.size(DsMetrics.iconButtonGlyph)
                )
            }
        )
    }
}
