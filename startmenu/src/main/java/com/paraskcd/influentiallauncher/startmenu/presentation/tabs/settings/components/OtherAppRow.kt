// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.components

import android.content.pm.PackageManager
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.graphics.drawable.toBitmap
import com.composables.icons.lucide.ExternalLink
import com.composables.icons.lucide.LayoutGrid
import com.composables.icons.lucide.Lucide
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.presentation.model.OtherApp
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.SettingsMetrics

@Composable
fun OtherAppRow(app: OtherApp, index: Int, count: Int, onOpen: () -> Unit) {
    val context = LocalContext.current
    val colors = InfTheme.colors
    val name = stringResource(app.nameRes)
    val iconPx = with(LocalDensity.current) { SettingsMetrics.iconWell.roundToPx() }
    val icon = remember(context, app.packageName, iconPx) {
        try {
            context.packageManager.getApplicationIcon(app.packageName).toBitmap(iconPx, iconPx).asImageBitmap()
        } catch (_: PackageManager.NameNotFoundException) {
            null
        }
    }
    InfGroupedCard(index = index, count = count) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(InfSpacing.s3),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = SettingsMetrics.rowMinHeight)
                .clickable(onClickLabel = name, onClick = onOpen)
                .padding(horizontal = InfSpacing.s4)
        ) {
            if (icon != null) {
                Image(bitmap = icon, contentDescription = null, modifier = Modifier.size(SettingsMetrics.iconWell).clip(CircleShape))
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(SettingsMetrics.iconWell)
                        .background(colors.brandText.copy(alpha = SettingsMetrics.iconWellAlpha), CircleShape)
                ) {
                    Icon(imageVector = Lucide.LayoutGrid, contentDescription = null, tint = colors.brandText, modifier = Modifier.size(SettingsMetrics.icon))
                }
            }
            Column(modifier = Modifier.weight(1f).padding(vertical = InfSpacing.s3)) {
                Text(text = name, fontSize = SettingsMetrics.titleSize, fontWeight = FontWeight.SemiBold, color = colors.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = stringResource(app.captionRes), fontSize = SettingsMetrics.captionSize, color = colors.textSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Icon(imageVector = Lucide.ExternalLink, contentDescription = null, tint = colors.textTertiary, modifier = Modifier.size(SettingsMetrics.chevron))
        }
    }
}
