package com.paraskcd.influentiallauncher.devicestatus.domain.model

data class CellularStatus(
    val available: Boolean,
    val level: SignalLevel
)
