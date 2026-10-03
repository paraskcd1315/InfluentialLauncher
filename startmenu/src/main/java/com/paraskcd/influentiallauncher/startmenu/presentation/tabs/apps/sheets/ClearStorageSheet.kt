// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.apps.sheets

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.Eraser
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.X
import com.paraskcd.influentiallauncher.designsystem.organisms.InfAction
import com.paraskcd.influentiallauncher.designsystem.organisms.InfActionList
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.model.StartMenuApp
import com.paraskcd.influentiallauncher.windowing.presentation.InfSheetWindow

@Composable
fun ClearStorageSheet(
    entry: StartMenuApp?,
    onConfirm: (StartMenuApp) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = InfTheme.colors
    InfSheetWindow(
        item = entry,
        title = { stringResource(R.string.startmenu_clear_storage_title, it.app.label) },
        onDismiss = onDismiss
    ) { current ->
        InfActionList(
            actions = listOf(
                InfAction(Lucide.Eraser, stringResource(R.string.startmenu_clear_storage_confirm), colors.dangerText) { onConfirm(current) },
                InfAction(Lucide.X, stringResource(R.string.startmenu_clear_storage_cancel), colors.textPrimary) {}
            ),
            onDismiss = onDismiss
        )
    }
}
