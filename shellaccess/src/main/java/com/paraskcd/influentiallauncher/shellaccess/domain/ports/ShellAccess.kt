// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.domain.ports

import android.os.IBinder
import com.paraskcd.influentiallauncher.shellaccess.domain.model.ShellResult
import com.paraskcd.influentiallauncher.shellaccess.domain.model.ShellState
import kotlinx.coroutines.flow.StateFlow

interface ShellAccess {
    val state: StateFlow<ShellState>

    suspend fun run(command: String): ShellResult

    fun systemService(name: String): IBinder?

    fun ensureReady()

    fun startPairing(pairingCode: String)

    fun retry()
}
