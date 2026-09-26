package com.paraskcd.influentiallauncher.contacts.domain.ports

import android.graphics.Bitmap
import com.paraskcd.influentiallauncher.contacts.domain.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactsSource {
    val permission: String

    val callPermission: String

    fun hasPermission(): Boolean

    fun contacts(): Flow<List<Contact>>

    fun open(contact: Contact): Boolean

    fun call(contact: Contact): Boolean

    fun whatsApp(contact: Contact): Boolean

    suspend fun photo(contact: Contact, sizePx: Int): Bitmap?
}
