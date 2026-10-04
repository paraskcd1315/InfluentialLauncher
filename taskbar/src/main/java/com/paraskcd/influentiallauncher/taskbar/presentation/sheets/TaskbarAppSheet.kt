// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.taskbar.presentation.sheets

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.Activity
import com.composables.icons.lucide.Ban
import com.composables.icons.lucide.Eraser
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.LayoutGrid
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Move
import com.composables.icons.lucide.PinOff
import com.composables.icons.lucide.Shapes
import com.composables.icons.lucide.Trash2
import com.composables.icons.lucide.X
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.designsystem.atoms.InfAsyncIcon
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.organisms.InfAction
import com.paraskcd.influentiallauncher.designsystem.organisms.InfActionList
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.taskbar.R
import com.paraskcd.influentiallauncher.windowing.presentation.InfSheetWindow

@Composable
fun TaskbarAppSheet(
    app: LauncherApp?,
    onStart: Boolean,
    isOpen: Boolean,
    isRunning: Boolean = false,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onUnpin: (AppId) -> Unit,
    onToggleStart: (AppId) -> Unit,
    onInfo: (AppId) -> Unit,
    onUninstall: (AppId) -> Unit,
    onClose: (AppId) -> Unit,
    onForceStop: (AppId) -> Unit,
    onClearStorage: (LauncherApp) -> Unit,
    onDataUsage: (LauncherApp) -> Unit,
    onIcon: (LauncherApp) -> Unit
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
        val id = current.id
        InfActionList(
            actions = buildList {
                add(InfAction(Lucide.Move, stringResource(R.string.taskbar_edit), colors.textPrimary) { onEdit() })
                add(InfAction(Lucide.PinOff, stringResource(R.string.taskbar_unpin), colors.textPrimary) { onUnpin(id) })
                add(
                    InfAction(
                        icon = Lucide.LayoutGrid,
                        label = stringResource(if (onStart) R.string.taskbar_unpin_start else R.string.taskbar_pin_start),
                        tint = colors.textPrimary,
                        run = { onToggleStart(id) }
                    )
                )
                add(InfAction(Lucide.Shapes, stringResource(R.string.taskbar_icon), colors.textPrimary) { onIcon(current) })
                if (isOpen) {
                    add(InfAction(Lucide.X, stringResource(R.string.taskbar_close), colors.textPrimary) { onClose(id) })
                }
                if (isOpen || isRunning) {
                    add(InfAction(Lucide.Ban, stringResource(R.string.taskbar_force_stop), colors.textPrimary) { onForceStop(id) })
                }
                add(InfAction(Lucide.Activity, stringResource(R.string.taskbar_data_usage), colors.textPrimary) { onDataUsage(current) })
                add(InfAction(Lucide.Info, stringResource(R.string.taskbar_info), colors.textPrimary) { onInfo(id) })
                add(InfAction(Lucide.Eraser, stringResource(R.string.taskbar_clear_storage), colors.dangerText) { onClearStorage(current) })
                add(InfAction(Lucide.Trash2, stringResource(R.string.taskbar_uninstall), colors.dangerText) { onUninstall(id) })
            },
            onDismiss = onDismiss
        )
    }
}
