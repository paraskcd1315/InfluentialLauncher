package com.paraskcd.influentiallauncher.startmenu.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.Dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.startmenu.presentation.components.StartMenu.StartMenu
import com.paraskcd.influentiallauncher.startmenu.presentation.viewmodels.StartMenuViewModel
import com.paraskcd.influentiallauncher.startmenu.presentation.windows.StartMenuWindow

@Composable
fun StartMenuHost(
    open: Boolean,
    offsetY: Dp,
    horizontalMargin: Dp,
    onClose: () -> Unit,
    viewModel: StartMenuViewModel = hiltViewModel()
) {
    val sections by viewModel.sections.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()

    LaunchedEffect(open) {
        if (!open) viewModel.setQuery("")
    }

    StartMenuWindow(open = open, offsetY = offsetY, horizontalMargin = horizontalMargin, onClose = onClose) {
        StartMenu(
            sections = sections,
            query = query,
            onQueryChange = viewModel::setQuery,
            loadIcon = viewModel::icon,
            onLaunch = { id, bounds ->
                onClose()
                viewModel.launch(id, bounds)
            },
            onTogglePin = viewModel::togglePin,
            onInfo = { id, bounds ->
                onClose()
                viewModel.openInfo(id, bounds)
            },
            onUninstall = { id ->
                onClose()
                viewModel.uninstall(id)
            }
        )
    }
}
