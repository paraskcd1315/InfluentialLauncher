package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.apps

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.paraskcd.influentiallauncher.designsystem.atoms.InfSearchField
import com.paraskcd.influentiallauncher.designsystem.atoms.InfSectionHeader
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.pins.domain.model.PinTarget
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.shared.components.LetterIndexedBox
import com.paraskcd.influentiallauncher.startmenu.presentation.shared.components.ListSkeleton
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.apps.components.AppRow
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.apps.components.PinnedGrid
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.LetterIndex
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.ListKeys
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics
import com.paraskcd.influentiallauncher.startmenu.presentation.viewmodels.AppsViewModel
import kotlinx.coroutines.launch

@Composable
fun AppsTab(
    open: Boolean,
    onClose: () -> Unit,
    viewModel: AppsViewModel = hiltViewModel()
) {
    val content by viewModel.content.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val tint = InfTheme.colors.brandText.toArgb()
    val loadIcon: suspend (AppId, Int) -> Bitmap? = remember(tint) { { id, px -> viewModel.icon(id, px, tint) } }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var expandedKey by remember { mutableStateOf<String?>(null) }
    val listTop = StartMenuMetrics.searchTop + DsMetrics.searchHeight + StartMenuMetrics.searchContentGap

    LaunchedEffect(open) {
        if (!open) {
            viewModel.setQuery("")
            expandedKey = null
        }
    }
    LaunchedEffect(query) {
        expandedKey = null
        listState.scrollToItem(0)
    }

    val launch: (AppId, Rect?) -> Unit = { id, bounds ->
        onClose()
        viewModel.launch(id, bounds)
    }
    val info: (AppId, Rect?) -> Unit = { id, bounds ->
        onClose()
        viewModel.openInfo(id, bounds)
    }
    val uninstall: (AppId) -> Unit = { id ->
        onClose()
        viewModel.uninstall(id)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val current = content
        val sections = current?.sections.orEmpty()
        val available = sections.map { it.letter }.toSet()
        val leading = if (current?.pinned.isNullOrEmpty()) 1 else 3
        LetterIndexedBox(
            letters = LetterIndex.Letters,
            available = available,
            scrubberPadding = PaddingValues(top = listTop, bottom = StartMenuMetrics.listBottom),
            onJump = { letter ->
                val index = LetterIndex.headerIndices(leading, sections.map { it.letter to it.apps.size })[letter]
                if (index != null) scope.launch { listState.scrollToItem(index) }
            }
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
                            PinnedGrid(
                                apps = current.pinned,
                                expandedKey = expandedKey?.takeIf { it.startsWith(ListKeys.PinnedPrefix) }?.removePrefix(ListKeys.PinnedPrefix),
                                loadIcon = loadIcon,
                                onLaunch = launch,
                                onExpand = { key -> expandedKey = key?.let { ListKeys.PinnedPrefix + it } },
                                onToggleStart = { viewModel.togglePin(PinTarget.Start, it) },
                                onToggleTaskbar = { viewModel.togglePin(PinTarget.Taskbar, it) },
                                onInfo = { info(it, null) },
                                onUninstall = uninstall
                            )
                        }
                    }
                    item(key = ListKeys.AllAppsHeader) { InfSectionHeader(text = stringResource(R.string.startmenu_all_apps)) }
                    sections.forEach { section ->
                        item(key = ListKeys.HeaderPrefix + section.letter) { InfSectionHeader(text = section.letter.toString()) }
                        itemsIndexed(section.apps, key = { _, entry -> entry.app.id.key }) { index, entry ->
                            val key = entry.app.id.key
                            AppRow(
                                entry = entry,
                                index = index,
                                count = section.apps.size,
                                expanded = expandedKey == key,
                                loadIcon = loadIcon,
                                onLaunch = launch,
                                onLongPress = { expandedKey = if (expandedKey == key) null else key },
                                onToggleStart = { viewModel.togglePin(PinTarget.Start, entry.app.id) },
                                onToggleTaskbar = { viewModel.togglePin(PinTarget.Taskbar, entry.app.id) },
                                onInfo = { bounds -> info(entry.app.id, bounds) },
                                onUninstall = { uninstall(entry.app.id) }
                            )
                        }
                    }
                }
            }
        }
        InfSearchField(
            value = query,
            onValueChange = viewModel::setQuery,
            placeholder = stringResource(R.string.startmenu_search),
            clearDescription = stringResource(R.string.startmenu_clear),
            modifier = Modifier.padding(top = StartMenuMetrics.searchTop, start = StartMenuMetrics.listPadding, end = StartMenuMetrics.listPadding)
        )
    }
}
