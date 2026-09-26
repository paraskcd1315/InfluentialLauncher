package com.paraskcd.influentiallauncher.devicestatus.domain.model

data class WifiStatus(
    val connected: Boolean,
    val level: SignalLevel
)
