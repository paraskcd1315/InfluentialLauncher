package com.paraskcd.influentiallauncher.controlcenter.domain.model

data class ControlState(
    val toggles: Map<QuickToggle, Boolean>,
    val brightness: Float,
    val autoBrightness: Boolean,
    val volume: Float,
    val access: ShellAccess
)
