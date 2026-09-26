package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.contacts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.designsystem.atoms.InfSearchField
import com.paraskcd.influentiallauncher.designsystem.atoms.InfSectionHeader
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.model.PermissionState
import com.paraskcd.influentiallauncher.startmenu.presentation.shared.components.LetterIndexedBox
import com.paraskcd.influentiallauncher.startmenu.presentation.shared.components.ListSkeleton
import com.paraskcd.influentiallauncher.startmenu.presentation.shared.components.PermissionPrompt
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.contacts.components.ContactRow
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.LetterIndex
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.ListKeys
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics
import com.paraskcd.influentiallauncher.startmenu.presentation.viewmodels.ContactsViewModel
import kotlinx.coroutines.launch

@Composable
fun ContactsTab(
    open: Boolean,
    onClose: () -> Unit,
    viewModel: ContactsViewModel = hiltViewModel()
) {
    val permission by viewModel.permissionState.collectAsStateWithLifecycle()
    val sections by viewModel.sections.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val listTop = StartMenuMetrics.searchTop + DsMetrics.searchHeight + StartMenuMetrics.searchContentGap
    val contentPadding = PaddingValues(
        start = StartMenuMetrics.listPadding,
        end = StartMenuMetrics.listPadding + DsMetrics.scrubberWidth,
        top = listTop,
        bottom = StartMenuMetrics.listBottom
    )

    LaunchedEffect(open) {
        if (open) viewModel.refreshPermission() else viewModel.setQuery("")
    }
    LaunchedEffect(query) { listState.scrollToItem(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (permission == PermissionState.Missing) {
            PermissionPrompt(
                message = stringResource(R.string.startmenu_contacts_permission),
                permission = viewModel.permission,
                onResult = viewModel::refreshPermission,
                modifier = Modifier.padding(StartMenuMetrics.listPadding)
            )
            return@Box
        }
        val current = sections
        val lettered = current.orEmpty().filterNot { it.favourites }
        LetterIndexedBox(
            letters = LetterIndex.Letters,
            available = lettered.map { it.letter }.toSet(),
            scrubberPadding = PaddingValues(top = listTop, bottom = StartMenuMetrics.listBottom),
            onJump = { letter ->
                val favourites = current.orEmpty().firstOrNull { it.favourites }
                val leading = if (favourites == null) 0 else favourites.contacts.size + 1
                val index = LetterIndex.headerIndices(leading, lettered.map { it.letter to it.contacts.size })[letter]
                if (index != null) scope.launch { listState.scrollToItem(index) }
            }
        ) {
            when {
                current == null -> ListSkeleton(modifier = Modifier.padding(contentPadding))
                current.isEmpty() -> Text(
                    text = stringResource(R.string.startmenu_contacts_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = InfTheme.colors.textSecondary,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = listTop + StartMenuMetrics.listPadding)
                )
                else -> LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(StartMenuMetrics.rowGap),
                    contentPadding = contentPadding,
                    modifier = Modifier.fillMaxSize()
                ) {
                    current.forEach { section ->
                        item(key = ListKeys.HeaderPrefix + section.letter) {
                            val title = if (section.favourites) stringResource(R.string.startmenu_favourites) else section.letter.toString()
                            InfSectionHeader(text = title)
                        }
                        itemsIndexed(section.contacts, key = { _, contact -> "${section.letter}:${contact.id}" }) { index, contact ->
                            ContactRow(
                                contact = contact,
                                index = index,
                                count = section.contacts.size,
                                loadPhoto = viewModel::photo,
                                onOpen = {
                                    onClose()
                                    viewModel.open(it)
                                }
                            )
                        }
                    }
                }
            }
        }
        InfSearchField(
            value = query,
            onValueChange = viewModel::setQuery,
            placeholder = stringResource(R.string.startmenu_search_contacts),
            clearDescription = stringResource(R.string.startmenu_clear),
            modifier = Modifier.padding(top = StartMenuMetrics.searchTop, start = StartMenuMetrics.listPadding, end = StartMenuMetrics.listPadding)
        )
    }
}
