// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.tasks.domain.ports

interface AppActions {
    suspend fun forceStop(packageName: String)

    suspend fun clearStorage(packageName: String)
}
