// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.homescreen.presentation.sheets.AppIconSheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.designsystem.atoms.InfButton
import com.paraskcd.influentiallauncher.designsystem.atoms.InfSearchField
import com.paraskcd.influentiallauncher.designsystem.molecules.InfSegmented
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.homescreen.R
import com.paraskcd.influentiallauncher.homescreen.presentation.sheets.AppIconSheet.components.PackIconCell
import com.paraskcd.influentiallauncher.homescreen.presentation.utils.AppIconMetrics
import com.paraskcd.influentiallauncher.homescreen.presentation.utils.IconNameRanking
import com.paraskcd.influentiallauncher.homescreen.presentation.viewmodels.AppIconViewModel
import com.paraskcd.influentiallauncher.windowing.presentation.InfSheetWindow
import com.paraskcd.influentiallauncher.windowing.presentation.LocalWindowBlurred

@Composable
fun AppIconSheet(
    app: LauncherApp?,
    onDismiss: () -> Unit,
    viewModel: AppIconViewModel = hiltViewModel()
) {
    val packList by viewModel.packList.collectAsStateWithLifecycle()
    val pack by viewModel.pack.collectAsStateWithLifecycle()
    val names by viewModel.names.collectAsStateWithLifecycle()
    val style by viewModel.style.collectAsStateWithLifecycle()
    var query by rememberSaveable(app?.id?.key) { mutableStateOf("") }
    LaunchedEffect(app?.id) { app?.let { viewModel.open(it.id) } }
    InfSheetWindow(
        item = app,
        title = { it.label },
        onDismiss = onDismiss
    ) { current ->
        Column(verticalArrangement = Arrangement.spacedBy(InfSpacing.s4), modifier = Modifier.fillMaxWidth()) {
            val list = packList
            val note = when {
                list == null -> stringResource(R.string.home_icon_loading)
                list.isEmpty() -> stringResource(R.string.home_icon_no_packs)
                else -> null
            }
            if (note != null) {
                Text(text = note, style = MaterialTheme.typography.bodyMedium, color = InfTheme.colors.textSecondary)
            }
            if (!list.isNullOrEmpty()) {
                InfSegmented(
                    labels = list.map { it.label },
                    selected = list.indexOfFirst { it.packageName == pack }.coerceAtLeast(0),
                    onSelect = { viewModel.select(list[it].packageName) },
                    blurred = LocalWindowBlurred.current,
                    modifier = Modifier.fillMaxWidth()
                )
                InfSearchField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = stringResource(R.string.home_icon_search),
                    clearDescription = stringResource(R.string.home_icon_clear)
                )
                val shown = remember(names, query, current.label) { IconNameRanking.rank(names.orEmpty(), current.label, query) }
                val selected = pack
                if (selected != null) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(AppIconMetrics.columns),
                        modifier = Modifier.fillMaxWidth().heightIn(max = AppIconMetrics.gridMaxHeight)
                    ) {
                        items(shown, key = { it }) { name ->
                            PackIconCell(
                                key = "$selected/$name",
                                label = name,
                                load = { px -> viewModel.packIcon(selected, name, px) },
                                onPick = {
                                    viewModel.choose(current.id, name)
                                    onDismiss()
                                }
                            )
                        }
                    }
                }
            }
            if (style.choices[current.id] != null) {
                InfButton(
                    label = stringResource(R.string.home_icon_default),
                    onClick = {
                        viewModel.reset(current.id)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
