// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.model

import com.paraskcd.influentiallauncher.contacts.domain.model.Contact

data class ContactSection(
    val letter: Char,
    val contacts: List<Contact>
)
