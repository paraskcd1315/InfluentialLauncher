// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.statusbar.presentation.utils

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.lucide.BatteryCharging
import com.composables.icons.lucide.BatteryFull
import com.composables.icons.lucide.BatteryLow
import com.composables.icons.lucide.BatteryMedium
import com.composables.icons.lucide.BatteryWarning
import com.composables.icons.lucide.KeyRound
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.SignalHigh
import com.composables.icons.lucide.Signal
import com.composables.icons.lucide.SignalLow
import com.composables.icons.lucide.SignalMedium
import com.composables.icons.lucide.SignalZero
import com.composables.icons.lucide.Wifi
import com.composables.icons.lucide.WifiHigh
import com.composables.icons.lucide.WifiLow
import com.composables.icons.lucide.WifiOff
import com.composables.icons.lucide.WifiZero
import com.paraskcd.influentiallauncher.devicestatus.domain.model.BatteryStatus
import com.paraskcd.influentiallauncher.devicestatus.domain.model.SignalLevel
import com.paraskcd.influentiallauncher.devicestatus.domain.model.WifiStatus

object StatusIcons {
    private const val BatteryWarningBelow = 10
    private const val BatteryLowBelow = 30
    private const val BatteryMediumBelow = 70

    val vpn: ImageVector = Lucide.KeyRound

    fun battery(status: BatteryStatus): ImageVector = when {
        status.charging -> Lucide.BatteryCharging
        status.percent < BatteryWarningBelow -> Lucide.BatteryWarning
        status.percent < BatteryLowBelow -> Lucide.BatteryLow
        status.percent < BatteryMediumBelow -> Lucide.BatteryMedium
        else -> Lucide.BatteryFull
    }

    fun wifi(status: WifiStatus): ImageVector {
        if (!status.connected) return Lucide.WifiOff
        return when (status.level) {
            SignalLevel.None -> Lucide.WifiZero
            SignalLevel.Weak -> Lucide.WifiLow
            SignalLevel.Fair, SignalLevel.Good -> Lucide.WifiHigh
            SignalLevel.Excellent -> Lucide.Wifi
        }
    }

    fun cellular(level: SignalLevel): ImageVector = when (level) {
        SignalLevel.None -> Lucide.SignalZero
        SignalLevel.Weak -> Lucide.SignalLow
        SignalLevel.Fair -> Lucide.SignalMedium
        SignalLevel.Good -> Lucide.SignalHigh
        SignalLevel.Excellent -> Lucide.Signal
    }

    fun bars(level: SignalLevel): Int = level.ordinal
}
