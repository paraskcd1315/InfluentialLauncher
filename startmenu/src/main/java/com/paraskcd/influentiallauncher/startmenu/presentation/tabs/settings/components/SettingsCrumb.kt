// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.SettingsMetrics

@Composable
fun SettingsCrumb(label: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val colors = InfTheme.colors
    val root = stringResource(R.string.startmenu_tab_settings)
    Row(
        horizontalArrangement = Arrangement.spacedBy(InfSpacing.s2),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth().padding(bottom = InfSpacing.s2)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(InfSpacing.s1),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(InfShapes.md)
                .clickable(onClickLabel = root, onClick = onBack)
                .sizeIn(minHeight = DsMetrics.touchTargetMin)
                .padding(end = InfSpacing.s2)
        ) {
            Icon(imageVector = Lucide.ChevronLeft, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(SettingsMetrics.chevron))
            Text(text = root, fontSize = SettingsMetrics.titleSize, color = colors.textSecondary, maxLines = 1)
        }
        Icon(imageVector = Lucide.ChevronRight, contentDescription = null, tint = colors.textTertiary, modifier = Modifier.size(SettingsMetrics.chevron))
        Text(
            text = label,
            fontSize = SettingsMetrics.titleSize,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}
