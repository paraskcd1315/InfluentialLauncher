package com.paraskcd.influentiallauncher.startmenu.presentation.model

import com.paraskcd.influentiallauncher.contacts.domain.model.Contact

data class ContactSection(
    val letter: Char,
    val favourites: Boolean,
    val contacts: List<Contact>
)
