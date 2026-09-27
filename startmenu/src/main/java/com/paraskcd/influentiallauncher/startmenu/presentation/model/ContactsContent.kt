package com.paraskcd.influentiallauncher.startmenu.presentation.model

import com.paraskcd.influentiallauncher.contacts.domain.model.Contact

data class ContactsContent(
    val favourites: List<Contact>,
    val sections: List<ContactSection>
)
