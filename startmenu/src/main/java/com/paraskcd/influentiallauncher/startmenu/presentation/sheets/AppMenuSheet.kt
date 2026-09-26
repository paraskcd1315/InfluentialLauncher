package com.paraskcd.influentiallauncher.startmenu.presentation.sheets

import android.graphics.Bitmap
import android.view.Gravity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.LayoutGrid
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pin
import com.composables.icons.lucide.PinOff
import com.composables.icons.lucide.Trash2
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.designsystem.atoms.InfAsyncIcon
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.molecules.InfActionRow
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.designsystem.organisms.InfBottomSheet
import com.paraskcd.influentiallauncher.designsystem.theme.InfMotion
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.model.StartMenuApp
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics
import com.paraskcd.influentiallauncher.windowing.presentation.InfWindow
import kotlinx.coroutines.delay

@Composable
fun AppMenuSheet(
    entry: StartMenuApp?,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    onDismiss: () -> Unit,
    onToggleStart: (AppId) -> Unit,
    onToggleTaskbar: (AppId) -> Unit,
    onInfo: (AppId) -> Unit,
    onUninstall: (AppId) -> Unit
) {
    var retained by remember { mutableStateOf(entry) }
    var open by remember { mutableStateOf(false) }
    LaunchedEffect(entry) {
        if (entry != null) {
            retained = entry
            open = true
        } else {
            open = false
            delay(InfMotion.durPushMs.toLong())
            retained = null
        }
    }
    val current = retained ?: return
    val colors = InfTheme.colors
    val id = current.app.id
    InfWindow(
        cornerRadius = 0.dp,
        onDismissRequest = onDismiss,
        gravity = Gravity.TOP or Gravity.START,
        visible = open,
        focusable = true,
        fullScreen = true
    ) {
        InfBottomSheet(
            visible = open,
            onDismiss = onDismiss,
            title = current.app.label,
            leading = {
                InfAsyncIcon(key = id.key, size = DsMetrics.sheetIconSize, load = { loadIcon(id, it) }, version = loadIcon)
            }
        ) {
            val actions = listOf(
                SheetAction(
                    icon = Lucide.LayoutGrid,
                    label = stringResource(if (current.onStart) R.string.startmenu_unpin_start else R.string.startmenu_pin_start),
                    tint = colors.textPrimary,
                    run = { onToggleStart(id) }
                ),
                SheetAction(
                    icon = if (current.onTaskbar) Lucide.PinOff else Lucide.Pin,
                    label = stringResource(if (current.onTaskbar) R.string.startmenu_unpin_taskbar else R.string.startmenu_pin_taskbar),
                    tint = colors.textPrimary,
                    run = { onToggleTaskbar(id) }
                ),
                SheetAction(Lucide.Info, stringResource(R.string.startmenu_info), colors.textPrimary) { onInfo(id) },
                SheetAction(Lucide.Trash2, stringResource(R.string.startmenu_uninstall), colors.dangerText) { onUninstall(id) }
            )
            Column(verticalArrangement = Arrangement.spacedBy(StartMenuMetrics.rowGap)) {
                actions.forEachIndexed { index, action ->
                    InfGroupedCard(index = index, count = actions.size) {
                        InfActionRow(
                            icon = action.icon,
                            label = action.label,
                            tint = action.tint,
                            onClick = {
                                onDismiss()
                                action.run()
                            }
                        )
                    }
                }
            }
        }
    }
}
