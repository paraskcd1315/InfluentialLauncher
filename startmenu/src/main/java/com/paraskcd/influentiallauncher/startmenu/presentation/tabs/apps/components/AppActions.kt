package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.apps.components

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
import com.composables.icons.lucide.LayoutGrid
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pin
import com.composables.icons.lucide.PinOff
import com.composables.icons.lucide.Trash2
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.model.StartMenuApp
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics

@Composable
fun AppActions(
    entry: StartMenuApp,
    onToggleStart: () -> Unit,
    onToggleTaskbar: () -> Unit,
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
            .padding(start = StartMenuMetrics.rowPadding, end = StartMenuMetrics.rowPadding, bottom = StartMenuMetrics.rowPadding)
    ) {
        AppAction(
            icon = Lucide.LayoutGrid,
            label = stringResource(if (entry.onStart) R.string.startmenu_unpin_start else R.string.startmenu_pin_start),
            tint = colors.brandText,
            onClick = onToggleStart
        )
        AppAction(
            icon = if (entry.onTaskbar) Lucide.PinOff else Lucide.Pin,
            label = stringResource(if (entry.onTaskbar) R.string.startmenu_unpin_taskbar else R.string.startmenu_pin_taskbar),
            tint = colors.brandText,
            onClick = onToggleTaskbar
        )
        AppAction(icon = Lucide.Info, label = stringResource(R.string.startmenu_info), tint = colors.textPrimary, onClick = onInfo)
        AppAction(icon = Lucide.Trash2, label = stringResource(R.string.startmenu_uninstall), tint = colors.dangerText, onClick = onUninstall)
    }
}
