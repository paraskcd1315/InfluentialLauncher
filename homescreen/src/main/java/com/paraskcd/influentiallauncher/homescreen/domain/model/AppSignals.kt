// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.homescreen.domain.model

import com.paraskcd.influentiallauncher.apps.domain.model.AppId

data class AppSignals(val badges: Map<String, Int>, val openTasks: Map<String, Int>, val running: Set<String> = emptySet()) {
    fun badgeOf(id: AppId): Int = badges[id.packageName] ?: 0

    fun openOf(id: AppId): Int = openTasks[id.packageName] ?: 0

    fun runningOf(id: AppId): Boolean = id.packageName in running

    companion object {
        val None = AppSignals(emptyMap(), emptyMap())
    }
}
