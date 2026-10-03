// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.controlcenter.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.Ban
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Settings
import com.paraskcd.influentiallauncher.controlcenter.R
import com.paraskcd.influentiallauncher.controlcenter.domain.model.QuickToggle
import com.paraskcd.influentiallauncher.controlcenter.presentation.utils.ToggleVisuals
import com.paraskcd.influentiallauncher.designsystem.organisms.InfAction
import com.paraskcd.influentiallauncher.designsystem.organisms.InfActionList
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.windowing.presentation.InfSheetWindow

@Composable
fun ControlOptionsSheet(
    toggle: QuickToggle?,
    onOn: (QuickToggle) -> Unit,
    onOff: (QuickToggle) -> Unit,
    onOpenSettings: (QuickToggle) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = InfTheme.colors
    InfSheetWindow(
        item = toggle,
        title = { stringResource(ToggleVisuals.labelOf(it)) },
        onDismiss = onDismiss
    ) { current ->
        InfActionList(
            actions = listOf(
                InfAction(ToggleVisuals.iconOf(current), stringResource(R.string.controlcenter_option_on), colors.textPrimary) { onOn(current) },
                InfAction(Lucide.Ban, stringResource(R.string.controlcenter_option_off), colors.textPrimary) { onOff(current) },
                InfAction(Lucide.Settings, stringResource(R.string.controlcenter_option_settings), colors.textPrimary) { onOpenSettings(current) }
            ),
            onDismiss = onDismiss
        )
    }
}
