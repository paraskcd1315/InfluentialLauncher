// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.apps

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import com.paraskcd.influentiallauncher.windowing.presentation.isLandscape
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LaunchOrigin
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.designsystem.atoms.InfSectionHeader
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.pins.domain.model.PinTarget
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.apps.sheets.AppMenuSheet
import com.paraskcd.influentiallauncher.homescreen.presentation.sheets.AppIconSheet.AppIconSheet
import com.paraskcd.influentiallauncher.homescreen.presentation.sheets.ClearStorageSheet
import com.paraskcd.influentiallauncher.homescreen.presentation.sheets.DataUsageSheet
import com.paraskcd.influentiallauncher.startmenu.presentation.shared.components.LetterIndexedBox
import com.paraskcd.influentiallauncher.startmenu.presentation.shared.components.ListSkeleton
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.apps.components.AppGrid
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.LetterIndex
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.ListKeys
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics
import com.paraskcd.influentiallauncher.startmenu.presentation.viewmodels.AppsViewModel
import kotlinx.coroutines.launch

@Composable
fun AppsTab(
    open: Boolean,
    onClose: () -> Unit,
    onLaunched: () -> Unit,
    onScrub: (Char?) -> Unit,
    viewModel: AppsViewModel = hiltViewModel()
) {
    val content by viewModel.content.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val signals by viewModel.signals.collectAsStateWithLifecycle()
    val openPackages by viewModel.openPackages.collectAsStateWithLifecycle()
    val runningPackages by viewModel.runningPackages.collectAsStateWithLifecycle()
    val showLabels by viewModel.showLabels.collectAsStateWithLifecycle()
    val iconStyle by viewModel.iconStyle.collectAsStateWithLifecycle()
    val tint = InfTheme.colors.brandText.toArgb()
    val iconBackground = InfTheme.colors.glassStrongBg.toArgb()
    val loadIcon: suspend (AppId, Int) -> Bitmap? = remember(tint, iconBackground, iconStyle) { { id, px -> viewModel.icon(id, px, tint, iconBackground) } }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var menuKey by remember { mutableStateOf<String?>(null) }
    var iconApp by remember { mutableStateOf<LauncherApp?>(null) }
    var clearKey by remember { mutableStateOf<String?>(null) }
    var dataKey by remember { mutableStateOf<String?>(null) }
    val listTop = StartMenuMetrics.searchTop + DsMetrics.searchHeight + StartMenuMetrics.searchContentGap

    LaunchedEffect(open) {
        if (open) {
            listState.scrollToItem(0)
        } else {
            viewModel.setQuery("")
            menuKey = null
        }
    }
    LaunchedEffect(query) { listState.scrollToItem(0) }
    val pinnedKeys = content?.pinned?.map { it.app.id.key }
    LaunchedEffect(pinnedKeys) { listState.scrollToItem(0) }

    val launch: (AppId, LaunchOrigin?) -> Unit = { id, origin ->
        onLaunched()
        viewModel.launch(id, origin)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val current = content
        val sections = current?.sections.orEmpty()
        val available = sections.map { it.letter }.toSet()
        val leading = if (current?.pinned.isNullOrEmpty()) 1 else 3
        val gridColumns = if (isLandscape()) StartMenuMetrics.landscapePinnedColumns else StartMenuMetrics.pinnedColumns
        LetterIndexedBox(
            letters = LetterIndex.Letters,
            available = available,
            scrubberPadding = PaddingValues(top = listTop, bottom = StartMenuMetrics.listBottom),
            onJump = { letter ->
                val index = LetterIndex.headerIndices(leading, sections.map { it.letter to 1 })[letter]
                if (index != null) scope.launch { listState.scrollToItem(index) }
            },
            onScrub = onScrub
        ) {
            when {
                current == null -> ListSkeleton(
                    modifier = Modifier.padding(start = StartMenuMetrics.listPadding, end = StartMenuMetrics.listPadding + DsMetrics.scrubberWidth, top = listTop)
                )
                sections.isEmpty() -> Text(
                    text = stringResource(R.string.startmenu_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = InfTheme.colors.textSecondary,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = listTop + StartMenuMetrics.listPadding)
                )
                else -> LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(StartMenuMetrics.rowGap),
                    contentPadding = PaddingValues(
                        start = StartMenuMetrics.listPadding,
                        end = StartMenuMetrics.listPadding + DsMetrics.scrubberWidth,
                        top = listTop,
                        bottom = StartMenuMetrics.listBottom
                    ),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (current.pinned.isNotEmpty()) {
                        item(key = ListKeys.PinnedHeader) { InfSectionHeader(text = stringResource(R.string.startmenu_pinned)) }
                        item(key = ListKeys.PinnedGrid) {
                            AppGrid(
                                apps = current.pinned,
                                loadIcon = loadIcon,
                                onLaunch = launch,
                                onLongPress = { menuKey = it.app.id.key },
                                columns = gridColumns,
                                signals = signals,
                                showLabels = showLabels
                            )
                        }
                    }
                    item(key = ListKeys.AllAppsHeader) { InfSectionHeader(text = stringResource(R.string.startmenu_all_apps)) }
                    sections.forEach { section ->
                        item(key = ListKeys.HeaderPrefix + section.letter) { InfSectionHeader(text = section.letter.toString()) }
                        item(key = ListKeys.GridPrefix + section.letter) {
                            AppGrid(
                                apps = section.apps,
                                loadIcon = loadIcon,
                                onLaunch = launch,
                                onLongPress = { menuKey = it.app.id.key },
                                columns = gridColumns,
                                signals = signals,
                                showLabels = showLabels
                            )
                        }
                    }
                }
            }
        }
    }

    val menuEntry = menuKey?.let { key ->
        content?.let { current -> (current.pinned + current.sections.flatMap { it.apps }).firstOrNull { it.app.id.key == key } }
    }
    AppMenuSheet(
        entry = menuEntry,
        isOpen = menuEntry?.app?.id?.packageName in openPackages,
        isRunning = menuEntry?.app?.id?.packageName in runningPackages,
        loadIcon = loadIcon,
        onDismiss = { menuKey = null },
        onToggleStart = { viewModel.togglePin(PinTarget.Start, it) },
        onToggleTaskbar = { viewModel.togglePin(PinTarget.Taskbar, it) },
        onAddToHome = viewModel::addToHome,
        onInfo = { id ->
            onClose()
            viewModel.openInfo(id, null)
        },
        onUninstall = { id ->
            onClose()
            viewModel.uninstall(id)
        },
        onClose = viewModel::closeApp,
        onForceStop = viewModel::forceStop,
        onClearStorage = { id ->
            menuKey = null
            clearKey = id.key
        },
        onDataUsage = { id ->
            menuKey = null
            dataKey = id.key
        },
        onIcon = { entry ->
            menuKey = null
            iconApp = entry.app
        }
    )
    AppIconSheet(app = iconApp, onDismiss = { iconApp = null })
    val clearEntry = clearKey?.let { key ->
        content?.let { current -> (current.pinned + current.sections.flatMap { it.apps }).firstOrNull { it.app.id.key == key } }
    }
    ClearStorageSheet(
        target = clearEntry?.app?.id,
        label = clearEntry?.app?.label.orEmpty(),
        onConfirm = { clearEntry?.let { viewModel.clearStorage(it.app.id) } },
        onDismiss = { clearKey = null }
    )
    val dataEntry = dataKey?.let { key ->
        content?.let { current -> (current.pinned + current.sections.flatMap { it.apps }).firstOrNull { it.app.id.key == key } }
    }
    DataUsageSheet(
        target = dataEntry?.app?.id,
        label = dataEntry?.app?.label.orEmpty(),
        load = viewModel::dataUsage,
        onDismiss = { dataKey = null }
    )
}
