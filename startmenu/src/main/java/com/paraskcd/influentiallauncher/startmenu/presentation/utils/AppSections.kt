package com.paraskcd.influentiallauncher.startmenu.presentation.utils

import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.startmenu.presentation.model.AppSection
import com.paraskcd.influentiallauncher.startmenu.presentation.model.StartMenuApp
import java.text.Normalizer

object AppSections {
    const val OtherLetter = '#'
    const val HeaderKeyPrefix = "header:"
    val Letters: List<Char> = ('A'..'Z').toList() + OtherLetter
    private val Diacritics = Regex("\\p{Mn}+")

    fun of(apps: List<LauncherApp>, pins: List<AppId>, query: String): List<AppSection> {
        val needle = fold(query.trim())
        val pinned = pins.toSet()
        return apps
            .filter { needle.isEmpty() || fold(it.label).contains(needle) }
            .sortedBy { fold(it.label) }
            .groupBy { letterOf(it.label) }
            .toSortedMap(compareBy { Letters.indexOf(it) })
            .map { (letter, group) -> AppSection(letter, group.map { StartMenuApp(it, it.id in pinned) }) }
    }

    fun headerIndices(sections: List<AppSection>): Map<Char, Int> {
        var index = 0
        return sections.associate { section ->
            val header = section.letter to index
            index += section.apps.size + 1
            header
        }
    }

    fun letterOf(label: String): Char {
        val first = fold(label).firstOrNull()?.uppercaseChar() ?: return OtherLetter
        return if (first in 'A'..'Z') first else OtherLetter
    }

    private fun fold(text: String): String =
        Normalizer.normalize(text, Normalizer.Form.NFD).replace(Diacritics, "").lowercase()
}
