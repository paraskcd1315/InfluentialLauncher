// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.utils

import java.text.Normalizer

object LetterIndex {
    const val OtherLetter = '#'
    val Letters: List<Char> = ('A'..'Z').toList() + OtherLetter
    private val Diacritics = Regex("\\p{Mn}+")

    fun fold(text: String): String =
        Normalizer.normalize(text, Normalizer.Form.NFD).replace(Diacritics, "").lowercase()

    fun letterOf(label: String): Char {
        val first = fold(label).firstOrNull()?.uppercaseChar() ?: return OtherLetter
        return if (first in 'A'..'Z') first else OtherLetter
    }

    fun order(letter: Char): Int = Letters.indexOf(letter)

    fun headerIndices(leading: Int, sizes: List<Pair<Char, Int>>): Map<Char, Int> {
        var index = leading
        return sizes.associate { (letter, size) ->
            val header = letter to index
            index += size + 1
            header
        }
    }
}
