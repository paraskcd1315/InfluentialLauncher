// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.homescreen.presentation.sheets

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.Eraser
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.X
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.designsystem.organisms.InfAction
import com.paraskcd.influentiallauncher.designsystem.organisms.InfActionList
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.homescreen.R
import com.paraskcd.influentiallauncher.windowing.presentation.InfSheetWindow

@Composable
fun ClearStorageSheet(
    target: AppId?,
    label: String,
    onConfirm: (AppId) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = InfTheme.colors
    InfSheetWindow(
        item = target,
        title = { stringResource(R.string.home_clear_storage_title, label) },
        onDismiss = onDismiss
    ) { current ->
        InfActionList(
            actions = listOf(
                InfAction(Lucide.Eraser, stringResource(R.string.home_clear_storage_confirm), colors.dangerText) { onConfirm(current) },
                InfAction(Lucide.X, stringResource(R.string.home_clear_storage_cancel), colors.textPrimary) {}
            ),
            onDismiss = onDismiss
        )
    }
}
