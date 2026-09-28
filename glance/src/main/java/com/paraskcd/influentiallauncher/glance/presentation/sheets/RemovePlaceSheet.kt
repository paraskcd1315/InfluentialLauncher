// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.sheets

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Trash2
import com.composables.icons.lucide.X
import com.paraskcd.influentiallauncher.designsystem.organisms.InfAction
import com.paraskcd.influentiallauncher.designsystem.organisms.InfActionList
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.glance.R
import com.paraskcd.influentiallauncher.glance.presentation.utils.PlaceLabels
import com.paraskcd.influentiallauncher.weather.domain.model.Place
import com.paraskcd.influentiallauncher.windowing.presentation.InfSheetWindow

@Composable
fun RemovePlaceSheet(
    place: Place?,
    onConfirm: (Place) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = InfTheme.colors
    InfSheetWindow(
        item = place,
        title = { stringResource(R.string.weather_remove_title, PlaceLabels.nameOf(it)) },
        onDismiss = onDismiss
    ) { current ->
        InfActionList(
            actions = listOf(
                InfAction(Lucide.Trash2, stringResource(R.string.weather_remove_confirm), colors.dangerText) { onConfirm(current) },
                InfAction(Lucide.X, stringResource(R.string.weather_remove_cancel), colors.textPrimary) {}
            ),
            onDismiss = onDismiss
        )
    }
}
