package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.contacts

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.contacts.domain.model.Contact
import com.paraskcd.influentiallauncher.designsystem.atoms.InfSectionHeader
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.model.PermissionState
import com.paraskcd.influentiallauncher.startmenu.presentation.shared.components.LetterIndexedBox
import com.paraskcd.influentiallauncher.startmenu.presentation.shared.components.ListSkeleton
import com.paraskcd.influentiallauncher.startmenu.presentation.shared.components.PermissionPrompt
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.contacts.components.ContactRow
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.contacts.components.PinnedContacts
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.contacts.sheets.ContactMenuSheet
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.LetterIndex
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.ListKeys
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics
import com.paraskcd.influentiallauncher.startmenu.presentation.viewmodels.ContactsViewModel
import kotlinx.coroutines.launch

@Composable
fun ContactsTab(
    open: Boolean,
    onClose: () -> Unit,
    onScrub: (Char?) -> Unit,
    viewModel: ContactsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val permission by viewModel.permissionState.collectAsStateWithLifecycle()
    val content by viewModel.content.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var menuKey by remember { mutableStateOf<String?>(null) }
    var pendingCall by remember { mutableStateOf<Contact?>(null) }
    val callLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        pendingCall?.let(viewModel::call)
        pendingCall = null
    }
    val listTop = StartMenuMetrics.searchTop + DsMetrics.searchHeight + StartMenuMetrics.searchContentGap
    val contentPadding = PaddingValues(
        start = StartMenuMetrics.listPadding,
        end = StartMenuMetrics.listPadding + DsMetrics.scrubberWidth,
        top = listTop,
        bottom = StartMenuMetrics.listBottom
    )

    LaunchedEffect(open) {
        if (open) {
            viewModel.refreshPermission()
            listState.scrollToItem(0)
        } else {
            viewModel.setQuery("")
            menuKey = null
        }
    }
    LaunchedEffect(query) { listState.scrollToItem(0) }
    val pinnedKeys = content?.pinned?.map { it.lookupKey }
    LaunchedEffect(pinnedKeys) { listState.scrollToItem(0) }

    val openContact: (Contact) -> Unit = {
        onClose()
        viewModel.open(it)
    }
    val call: (Contact) -> Unit = { contact ->
        if (ContextCompat.checkSelfPermission(context, viewModel.callPermission) == PackageManager.PERMISSION_GRANTED) {
            onClose()
            viewModel.call(contact)
        } else {
            onClose()
            pendingCall = contact
            callLauncher.launch(viewModel.callPermission)
        }
    }
    val whatsApp: (Contact) -> Unit = {
        onClose()
        viewModel.whatsApp(it)
    }
    val longPress: (Contact) -> Unit = { menuKey = it.lookupKey }

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
        val current = content
        val sections = current?.sections.orEmpty()
        val pinned = current?.pinned.orEmpty()
        val lettered = sections.filterNot { it.favourites }
        LetterIndexedBox(
            letters = LetterIndex.Letters,
            available = lettered.map { it.letter }.toSet(),
            scrubberPadding = PaddingValues(top = listTop, bottom = StartMenuMetrics.listBottom),
            onJump = { letter ->
                val favourites = sections.firstOrNull { it.favourites }
                val pinnedItems = if (pinned.isEmpty()) 0 else 2
                val leading = pinnedItems + if (favourites == null) 0 else favourites.contacts.size + 1
                val index = LetterIndex.headerIndices(leading, lettered.map { it.letter to it.contacts.size })[letter]
                if (index != null) scope.launch { listState.scrollToItem(index) }
            },
            onScrub = onScrub
        ) {
            when {
                current == null -> ListSkeleton(modifier = Modifier.padding(contentPadding))
                sections.isEmpty() -> Text(
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
                    if (pinned.isNotEmpty()) {
                        item(key = ListKeys.PinnedHeader) { InfSectionHeader(text = stringResource(R.string.startmenu_pinned)) }
                        item(key = ListKeys.PinnedGrid) {
                            PinnedContacts(contacts = pinned, loadPhoto = viewModel::photo, onOpen = openContact, onLongPress = longPress)
                        }
                    }
                    sections.forEach { section ->
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
                                onOpen = openContact,
                                onLongPress = longPress,
                                onCall = call,
                                onWhatsApp = whatsApp
                            )
                        }
                    }
                }
            }
        }
    }

    val menuContact = menuKey?.let { key -> content?.sections?.flatMap { it.contacts }?.firstOrNull { it.lookupKey == key } }
    ContactMenuSheet(
        contact = menuContact,
        pinned = menuKey != null && content?.pinned?.any { it.lookupKey == menuKey } == true,
        loadPhoto = viewModel::photo,
        onDismiss = { menuKey = null },
        onTogglePin = viewModel::togglePin,
        onCall = call,
        onWhatsApp = whatsApp,
        onOpen = openContact
    )
}
