package com.paraskcd.influentiallauncher.startmenu.presentation.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.contacts.domain.model.Contact
import com.paraskcd.influentiallauncher.contacts.domain.ports.ContactPinStore
import com.paraskcd.influentiallauncher.contacts.domain.ports.ContactsSource
import com.paraskcd.influentiallauncher.startmenu.presentation.model.ContactsContent
import com.paraskcd.influentiallauncher.startmenu.presentation.model.PermissionState
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.ContactSections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val contactsSource: ContactsSource,
    private val pinStore: ContactPinStore
) : ViewModel() {

    val permission: String = contactsSource.permission

    val callPermission: String = contactsSource.callPermission

    private val _permissionState = MutableStateFlow(currentPermission())
    val permissionState: StateFlow<PermissionState> = _permissionState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val content: StateFlow<ContactsContent?> = _permissionState
        .flatMapLatest { state ->
            if (state == PermissionState.Granted) {
                combine(contactsSource.contacts(), pinStore.pins(), _query) { contacts, pins, query ->
                    val byKey = contacts.associateBy { it.lookupKey }
                    ContactsContent(
                        pinned = if (query.isBlank()) pins.mapNotNull { byKey[it] } else emptyList(),
                        sections = ContactSections.of(contacts, query)
                    )
                }
            } else {
                emptyFlow()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), null)

    fun setQuery(value: String) {
        _query.value = value
    }

    fun refreshPermission() {
        _permissionState.value = currentPermission()
    }

    fun open(contact: Contact) {
        contactsSource.open(contact)
    }

    fun call(contact: Contact) {
        contactsSource.call(contact)
    }

    fun whatsApp(contact: Contact) {
        contactsSource.whatsApp(contact)
    }

    fun togglePin(contact: Contact) {
        viewModelScope.launch { pinStore.toggle(contact.lookupKey) }
    }

    suspend fun photo(contact: Contact, sizePx: Int): Bitmap? = contactsSource.photo(contact, sizePx)

    private fun currentPermission(): PermissionState =
        if (contactsSource.hasPermission()) PermissionState.Granted else PermissionState.Missing

    private companion object {
        const val StopTimeoutMs = 5_000L
    }
}
