// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.homescreen.presentation.sheets

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Move
import com.composables.icons.lucide.Pin
import com.composables.icons.lucide.PinOff
import com.composables.icons.lucide.SquareMinus
import com.composables.icons.lucide.Trash2
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.designsystem.atoms.InfAsyncIcon
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.organisms.InfAction
import com.paraskcd.influentiallauncher.designsystem.organisms.InfActionList
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.homescreen.R
import com.paraskcd.influentiallauncher.windowing.presentation.InfSheetWindow

@Composable
fun HomeAppSheet(
    app: LauncherApp?,
    onTaskbar: Boolean,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    onEdit: () -> Unit,
    onToggleTaskbar: (AppId) -> Unit,
    onRemove: (LauncherApp) -> Unit,
    onInfo: (AppId) -> Unit,
    onUninstall: (AppId) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = InfTheme.colors
    InfSheetWindow(
        item = app,
        title = { it.label },
        onDismiss = onDismiss,
        leading = { current ->
            InfAsyncIcon(key = current.id.key, size = DsMetrics.sheetIconSize, load = { loadIcon(current.id, it) }, version = loadIcon)
        }
    ) { current ->
        InfActionList(
            actions = listOf(
                InfAction(Lucide.Move, stringResource(R.string.home_edit), colors.textPrimary, onEdit),
                InfAction(
                    icon = if (onTaskbar) Lucide.PinOff else Lucide.Pin,
                    label = stringResource(if (onTaskbar) R.string.home_unpin_taskbar else R.string.home_pin_taskbar),
                    tint = colors.textPrimary,
                    run = { onToggleTaskbar(current.id) }
                ),
                InfAction(Lucide.SquareMinus, stringResource(R.string.home_remove_confirm), colors.textPrimary) { onRemove(current) },
                InfAction(Lucide.Info, stringResource(R.string.home_info), colors.textPrimary) { onInfo(current.id) },
                InfAction(Lucide.Trash2, stringResource(R.string.home_uninstall), colors.dangerText) { onUninstall(current.id) }
            ),
            onDismiss = onDismiss
        )
    }
}
