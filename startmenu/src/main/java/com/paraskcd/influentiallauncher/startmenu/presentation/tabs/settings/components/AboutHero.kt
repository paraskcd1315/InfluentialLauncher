// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.core.graphics.drawable.toBitmap
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.SettingsMetrics

@Composable
fun AboutHero(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val colors = InfTheme.colors
    val iconPx = with(LocalDensity.current) { SettingsMetrics.aboutIcon.roundToPx() }
    val name = remember(context) { context.applicationInfo.loadLabel(context.packageManager).toString() }
    val icon = remember(context, iconPx) { context.packageManager.getApplicationIcon(context.packageName).toBitmap(iconPx, iconPx).asImageBitmap() }
    val info = remember(context) { runCatching { context.packageManager.getPackageInfo(context.packageName, 0) }.getOrNull() }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SettingsMetrics.aboutSpacing),
        modifier = modifier.fillMaxWidth().padding(vertical = SettingsMetrics.aboutPadding)
    ) {
        Image(bitmap = icon, contentDescription = null, modifier = Modifier.size(SettingsMetrics.aboutIcon).clip(CircleShape))
        Text(text = name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = colors.textPrimary)
        Text(
            text = stringResource(R.string.startmenu_about_version, info?.versionName.orEmpty(), info?.longVersionCode ?: 0L),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary
        )
        Text(
            text = stringResource(R.string.startmenu_about_description),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}
