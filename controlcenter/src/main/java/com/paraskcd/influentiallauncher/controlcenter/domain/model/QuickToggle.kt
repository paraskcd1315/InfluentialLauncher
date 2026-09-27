package com.paraskcd.influentiallauncher.controlcenter.domain.model

enum class QuickToggle(val needsShell: Boolean) {
    Wifi(needsShell = true),
    Bluetooth(needsShell = true),
    Airplane(needsShell = true),
    MobileData(needsShell = true),
    Flashlight(needsShell = false),
    DoNotDisturb(needsShell = true),
    BatterySaver(needsShell = true),
    NightLight(needsShell = true),
    DarkMode(needsShell = true),
    RotationLock(needsShell = true),
    Location(needsShell = true)
}
