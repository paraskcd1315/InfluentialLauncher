package com.paraskcd.influentiallauncher.contacts.domain.ports

import kotlinx.coroutines.flow.Flow

interface ContactPinStore {
    fun pins(): Flow<List<String>>

    suspend fun toggle(lookupKey: String)
}
