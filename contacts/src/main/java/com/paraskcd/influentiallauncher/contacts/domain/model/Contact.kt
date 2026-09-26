package com.paraskcd.influentiallauncher.contacts.domain.model

data class Contact(
    val id: Long,
    val lookupKey: String,
    val name: String,
    val starred: Boolean,
    val photoUri: String?,
    val phone: String?,
    val whatsAppDataId: Long?
)
