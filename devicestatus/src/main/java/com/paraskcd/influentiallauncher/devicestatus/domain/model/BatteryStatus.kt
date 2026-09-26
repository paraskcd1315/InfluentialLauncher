package com.paraskcd.influentiallauncher.devicestatus.domain.model

data class BatteryStatus(
    val percent: Int,
    val charging: Boolean
)
