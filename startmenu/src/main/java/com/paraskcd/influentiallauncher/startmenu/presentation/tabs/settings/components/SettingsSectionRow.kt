// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.presentation.model.SettingsSection
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.SettingsMetrics

@Composable
fun SettingsSectionRow(section: SettingsSection, index: Int, count: Int, onOpen: () -> Unit) {
    val colors = InfTheme.colors
    val title = stringResource(section.titleRes)
    InfGroupedCard(index = index, count = count) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(InfSpacing.s3),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = SettingsMetrics.rowMinHeight)
                .clickable(onClickLabel = title, onClick = onOpen)
                .padding(horizontal = InfSpacing.s4)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(SettingsMetrics.iconWell)
                    .background(colors.brandText.copy(alpha = SettingsMetrics.iconWellAlpha), CircleShape)
            ) {
                Icon(imageVector = section.icon, contentDescription = null, tint = colors.brandText, modifier = Modifier.size(SettingsMetrics.icon))
            }
            Column(modifier = Modifier.weight(1f).padding(vertical = InfSpacing.s3)) {
                Text(
                    text = title,
                    fontSize = SettingsMetrics.titleSize,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(section.captionRes),
                    fontSize = SettingsMetrics.captionSize,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(imageVector = Lucide.ChevronRight, contentDescription = null, tint = colors.textTertiary, modifier = Modifier.size(SettingsMetrics.chevron))
        }
    }
}
