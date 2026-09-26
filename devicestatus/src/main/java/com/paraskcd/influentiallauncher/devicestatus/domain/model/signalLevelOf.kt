package com.paraskcd.influentiallauncher.devicestatus.domain.model

fun signalLevelOf(bars: Int): SignalLevel =
    SignalLevel.entries[bars.coerceIn(0, SignalLevel.entries.lastIndex)]
