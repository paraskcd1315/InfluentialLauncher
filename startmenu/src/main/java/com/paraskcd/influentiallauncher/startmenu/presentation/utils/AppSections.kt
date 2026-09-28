// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.utils

import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.startmenu.presentation.model.AppSection
import com.paraskcd.influentiallauncher.startmenu.presentation.model.StartMenuApp
import com.paraskcd.influentiallauncher.startmenu.presentation.model.StartMenuContent

object AppSections {
    fun of(apps: List<LauncherApp>, taskbarPins: List<AppId>, startPins: List<AppId>, query: String): StartMenuContent {
        val needle = LetterIndex.fold(query.trim())
        val onTaskbar = taskbarPins.toSet()
        val onStart = startPins.toSet()
        fun entry(app: LauncherApp) = StartMenuApp(app, app.id in onTaskbar, app.id in onStart)
        val byId = apps.associateBy { it.id }
        val pinned = if (needle.isEmpty()) startPins.mapNotNull { byId[it] }.map(::entry) else emptyList()
        val sections = apps
            .filter { needle.isEmpty() || LetterIndex.fold(it.label).contains(needle) }
            .sortedBy { LetterIndex.fold(it.label) }
            .groupBy { LetterIndex.letterOf(it.label) }
            .toSortedMap(compareBy(LetterIndex::order))
            .map { (letter, group) -> AppSection(letter, group.map(::entry)) }
        return StartMenuContent(pinned, sections)
    }
}
