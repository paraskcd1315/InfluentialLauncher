package com.paraskcd.influentiallauncher.homescreen.domain.model

import com.paraskcd.influentiallauncher.apps.domain.model.AppId

data class AppSignals(val badges: Map<String, Int>, val openTasks: Map<String, Int>) {
    fun badgeOf(id: AppId): Int = badges[id.packageName] ?: 0

    fun openOf(id: AppId): Int = openTasks[id.packageName] ?: 0

    companion object {
        val None = AppSignals(emptyMap(), emptyMap())
    }
}
