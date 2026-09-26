package com.paraskcd.influentiallauncher.startmenu.presentation.components.StartMenu.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pin
import com.composables.icons.lucide.PinOff
import com.composables.icons.lucide.Trash2
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics

@Composable
fun AppActions(
    pinned: Boolean,
    onTogglePin: () -> Unit,
    onInfo: () -> Unit,
    onUninstall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = InfTheme.colors
    Row(
        horizontalArrangement = Arrangement.spacedBy(StartMenuMetrics.actionGap),
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(start = StartMenuMetrics.rowPadding, end = StartMenuMetrics.rowPadding, bottom = StartMenuMetrics.actionGap)
    ) {
        AppAction(
            icon = if (pinned) Lucide.PinOff else Lucide.Pin,
            label = stringResource(if (pinned) R.string.startmenu_unpin else R.string.startmenu_pin),
            tint = colors.brandText,
            onClick = onTogglePin
        )
        AppAction(
            icon = Lucide.Info,
            label = stringResource(R.string.startmenu_info),
            tint = colors.textPrimary,
            onClick = onInfo
        )
        AppAction(
            icon = Lucide.Trash2,
            label = stringResource(R.string.startmenu_uninstall),
            tint = colors.dangerText,
            onClick = onUninstall
        )
    }
}
