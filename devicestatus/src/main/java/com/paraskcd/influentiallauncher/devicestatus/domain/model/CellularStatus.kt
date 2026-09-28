// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.devicestatus.domain.model

data class CellularStatus(
    val available: Boolean,
    val level: SignalLevel
)
