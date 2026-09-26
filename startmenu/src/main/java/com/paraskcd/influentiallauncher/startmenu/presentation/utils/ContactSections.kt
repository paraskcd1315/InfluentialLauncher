package com.paraskcd.influentiallauncher.startmenu.presentation.utils

import com.paraskcd.influentiallauncher.contacts.domain.model.Contact
import com.paraskcd.influentiallauncher.startmenu.presentation.model.ContactSection

object ContactSections {
    const val FavouritesLetter = '★'

    fun of(contacts: List<Contact>, query: String): List<ContactSection> {
        val needle = LetterIndex.fold(query.trim())
        val matching = contacts.filter { needle.isEmpty() || LetterIndex.fold(it.name).contains(needle) }
        val favourites = if (needle.isEmpty()) matching.filter { it.starred } else emptyList()
        val lettered = matching
            .sortedBy { LetterIndex.fold(it.name) }
            .groupBy { LetterIndex.letterOf(it.name) }
            .toSortedMap(compareBy(LetterIndex::order))
            .map { (letter, group) -> ContactSection(letter, favourites = false, contacts = group) }
        val head = if (favourites.isEmpty()) emptyList() else listOf(ContactSection(FavouritesLetter, favourites = true, contacts = favourites))
        return head + lettered
    }
}
