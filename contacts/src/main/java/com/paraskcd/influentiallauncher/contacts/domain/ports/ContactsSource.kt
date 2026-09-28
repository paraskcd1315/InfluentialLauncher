// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.contacts.domain.ports

import android.graphics.Bitmap
import com.paraskcd.influentiallauncher.contacts.domain.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactsSource {
    val permission: String

    val callPermission: String

    val writePermission: String

    fun hasPermission(): Boolean

    fun contacts(): Flow<List<Contact>>

    fun open(contact: Contact): Boolean

    fun call(contact: Contact): Boolean

    fun whatsApp(contact: Contact): Boolean

    suspend fun setFavourite(contact: Contact, favourite: Boolean): Boolean

    suspend fun photo(contact: Contact, sizePx: Int): Bitmap?
}
