package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.apps.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics

@Composable
fun AppAction(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(StartMenuMetrics.actionGap),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(StartMenuMetrics.actionHeight)
            .clip(InfShapes.pill)
            .background(tint.copy(alpha = StartMenuMetrics.actionFillAlpha))
            .clickable(onClick = onClick)
            .padding(horizontal = StartMenuMetrics.actionPadding)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(StartMenuMetrics.actionIconSize))
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = tint, maxLines = 1)
    }
}
