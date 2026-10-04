// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.homescreen.presentation.utils

import java.util.Locale

object IconNameRanking {
    private val NonLetters = Regex("[^a-z0-9]")

    fun rank(names: List<String>, label: String, query: String): List<String> {
        val wanted = normalized(query)
        val matching = if (wanted.isEmpty()) names else names.filter { normalized(it).contains(wanted) }
        val app = normalized(label)
        if (app.isEmpty()) return matching
        return matching.sortedByDescending { normalized(it).contains(app) }
    }

    private fun normalized(text: String): String = text.lowercase(Locale.ROOT).replace(NonLetters, "")
}
