package com.paraskcd.influentiallauncher.homescreen.presentation.sheets

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Trash2
import com.composables.icons.lucide.X
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.designsystem.organisms.InfAction
import com.paraskcd.influentiallauncher.designsystem.organisms.InfActionList
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.homescreen.R
import com.paraskcd.influentiallauncher.homescreen.presentation.state.VisualPage
import com.paraskcd.influentiallauncher.windowing.presentation.InfSheetWindow

@Composable
fun RemoveAppSheet(app: LauncherApp?, onConfirm: (LauncherApp) -> Unit, onDismiss: () -> Unit) {
    val colors = InfTheme.colors
    InfSheetWindow(
        item = app,
        title = { stringResource(R.string.home_remove_title, it.label) },
        onDismiss = onDismiss
    ) { current ->
        InfActionList(
            actions = listOf(
                InfAction(Lucide.Trash2, stringResource(R.string.home_remove_confirm), colors.dangerText) { onConfirm(current) },
                InfAction(Lucide.X, stringResource(R.string.home_cancel), colors.textPrimary) {}
            ),
            onDismiss = onDismiss
        )
    }
}

@Composable
fun DeletePageSheet(page: VisualPage?, pageNumber: Int, onConfirm: (VisualPage) -> Unit, onDismiss: () -> Unit) {
    val colors = InfTheme.colors
    InfSheetWindow(
        item = page,
        title = { stringResource(R.string.home_delete_title, pageNumber) },
        onDismiss = onDismiss
    ) { current ->
        InfActionList(
            actions = listOf(
                InfAction(Lucide.Trash2, stringResource(R.string.home_delete_confirm), colors.dangerText) { onConfirm(current) },
                InfAction(Lucide.X, stringResource(R.string.home_cancel), colors.textPrimary) {}
            ),
            onDismiss = onDismiss
        )
    }
}
