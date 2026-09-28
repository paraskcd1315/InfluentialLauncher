// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.contacts.domain.model

data class Contact(
    val id: Long,
    val lookupKey: String,
    val name: String,
    val starred: Boolean,
    val photoUri: String?,
    val phone: String?,
    val whatsAppDataId: Long?
) {
    val canWhatsApp: Boolean get() = whatsAppDataId != null || phone != null
}
