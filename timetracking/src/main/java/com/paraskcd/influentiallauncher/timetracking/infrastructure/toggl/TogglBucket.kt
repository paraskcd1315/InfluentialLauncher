// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.infrastructure.toggl

enum class TogglBucket(val hourlyBudget: Int, val reserve: Int) {
    User(hourlyBudget = 40, reserve = 60),
    Workspace(hourlyBudget = 60, reserve = 60);

    companion object {
        fun of(path: String): TogglBucket = if (path.startsWith(TogglApi.Paths.Me)) User else Workspace
    }
}
