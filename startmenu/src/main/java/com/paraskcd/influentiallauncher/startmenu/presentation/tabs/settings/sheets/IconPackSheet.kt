// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.paraskcd.influentiallauncher.apps.domain.model.IconPack
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.designsystem.molecules.InfSettingsRow
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.windowing.presentation.InfSheetWindow

@Composable
fun IconPackSheet(
    open: Boolean,
    packs: List<IconPack>,
    selected: String?,
    onPick: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf<IconPack?>(null) + packs
    InfSheetWindow(
        item = if (open) Unit else null,
        title = { stringResource(R.string.startmenu_settings_icon_pack) },
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(DsMetrics.groupGap)) {
            options.forEachIndexed { index, option ->
                InfGroupedCard(index = index, count = options.size) {
                    InfSettingsRow(
                        label = option?.label ?: stringResource(R.string.startmenu_settings_icon_pack_system),
                        modifier = Modifier.clickable {
                            onPick(option?.packageName)
                            onDismiss()
                        },
                        trailing = if (option?.packageName == selected) {
                            {
                                Icon(
                                    imageVector = Lucide.Check,
                                    contentDescription = null,
                                    tint = InfTheme.colors.brandText,
                                    modifier = Modifier.size(DsMetrics.actionIconSize)
                                )
                            }
                        } else {
                            null
                        }
                    )
                }
            }
        }
    }
}
