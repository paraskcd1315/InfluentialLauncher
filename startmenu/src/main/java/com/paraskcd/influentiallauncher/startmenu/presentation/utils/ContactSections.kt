package com.paraskcd.influentiallauncher.startmenu.presentation.utils

import com.paraskcd.influentiallauncher.contacts.domain.model.Contact
import com.paraskcd.influentiallauncher.startmenu.presentation.model.ContactSection

object ContactSections {
    fun of(contacts: List<Contact>, query: String): List<ContactSection> {
        val needle = LetterIndex.fold(query.trim())
        return contacts
            .filter { needle.isEmpty() || LetterIndex.fold(it.name).contains(needle) }
            .sortedBy { LetterIndex.fold(it.name) }
            .groupBy { LetterIndex.letterOf(it.name) }
            .toSortedMap(compareBy(LetterIndex::order))
            .map { (letter, group) -> ContactSection(letter, group) }
    }
}
