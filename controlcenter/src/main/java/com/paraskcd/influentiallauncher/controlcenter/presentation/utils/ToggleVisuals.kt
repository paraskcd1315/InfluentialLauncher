package com.paraskcd.influentiallauncher.controlcenter.presentation.utils

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.lucide.BellOff
import com.composables.icons.lucide.Bluetooth
import com.composables.icons.lucide.Contrast
import com.composables.icons.lucide.Flashlight
import com.composables.icons.lucide.Leaf
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.Plane
import com.composables.icons.lucide.Rotate3d
import com.composables.icons.lucide.Signal
import com.composables.icons.lucide.SunMoon
import com.composables.icons.lucide.Wifi
import com.paraskcd.influentiallauncher.controlcenter.R
import com.paraskcd.influentiallauncher.controlcenter.domain.model.QuickToggle

object ToggleVisuals {
    val order: List<QuickToggle> = listOf(
        QuickToggle.Wifi,
        QuickToggle.Bluetooth,
        QuickToggle.Airplane,
        QuickToggle.MobileData,
        QuickToggle.Flashlight,
        QuickToggle.DoNotDisturb,
        QuickToggle.BatterySaver,
        QuickToggle.NightLight,
        QuickToggle.DarkMode,
        QuickToggle.RotationLock,
        QuickToggle.Location
    )

    val withDetails: Set<QuickToggle> = setOf(QuickToggle.Wifi, QuickToggle.Bluetooth)

    fun iconOf(toggle: QuickToggle): ImageVector = when (toggle) {
        QuickToggle.Wifi -> Lucide.Wifi
        QuickToggle.Bluetooth -> Lucide.Bluetooth
        QuickToggle.Airplane -> Lucide.Plane
        QuickToggle.MobileData -> Lucide.Signal
        QuickToggle.Flashlight -> Lucide.Flashlight
        QuickToggle.DoNotDisturb -> Lucide.BellOff
        QuickToggle.BatterySaver -> Lucide.Leaf
        QuickToggle.NightLight -> Lucide.SunMoon
        QuickToggle.DarkMode -> Lucide.Contrast
        QuickToggle.RotationLock -> Lucide.Rotate3d
        QuickToggle.Location -> Lucide.MapPin
    }

    @StringRes
    fun labelOf(toggle: QuickToggle): Int = when (toggle) {
        QuickToggle.Wifi -> R.string.controlcenter_wifi
        QuickToggle.Bluetooth -> R.string.controlcenter_bluetooth
        QuickToggle.Airplane -> R.string.controlcenter_airplane
        QuickToggle.MobileData -> R.string.controlcenter_mobile_data
        QuickToggle.Flashlight -> R.string.controlcenter_flashlight
        QuickToggle.DoNotDisturb -> R.string.controlcenter_dnd
        QuickToggle.BatterySaver -> R.string.controlcenter_battery_saver
        QuickToggle.NightLight -> R.string.controlcenter_night_light
        QuickToggle.DarkMode -> R.string.controlcenter_dark_mode
        QuickToggle.RotationLock -> R.string.controlcenter_rotation_lock
        QuickToggle.Location -> R.string.controlcenter_location
    }
}
