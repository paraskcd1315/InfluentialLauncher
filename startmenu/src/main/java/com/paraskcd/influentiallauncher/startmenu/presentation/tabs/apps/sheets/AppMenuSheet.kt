package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.apps.sheets

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.LayoutGrid
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pin
import com.composables.icons.lucide.PinOff
import com.composables.icons.lucide.Trash2
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.designsystem.atoms.InfAsyncIcon
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.model.StartMenuApp
import com.paraskcd.influentiallauncher.startmenu.presentation.shared.sheets.SheetAction
import com.paraskcd.influentiallauncher.startmenu.presentation.shared.sheets.SheetActions
import com.paraskcd.influentiallauncher.startmenu.presentation.shared.sheets.SheetWindow

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
    val colors = InfTheme.colors
    SheetWindow(
        item = entry,
        title = { it.app.label },
        onDismiss = onDismiss,
        leading = { current ->
            InfAsyncIcon(key = current.app.id.key, size = DsMetrics.sheetIconSize, load = { loadIcon(current.app.id, it) }, version = loadIcon)
        }
    ) { current ->
        val id = current.app.id
        SheetActions(
            actions = listOf(
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
            ),
            onDismiss = onDismiss
        )
    }
}
