// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.domain.model

sealed interface ShellState {
    data object NotPaired : ShellState
    data object Pairing : ShellState
    data object WaitingForWifi : ShellState
    data object Starting : ShellState
    data object Ready : ShellState
    data class Failed(val reason: ShellFailure) : ShellState
}
