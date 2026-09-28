// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.model

import com.paraskcd.influentiallauncher.contacts.domain.model.Contact

data class ContactsContent(
    val favourites: List<Contact>,
    val sections: List<ContactSection>
)
