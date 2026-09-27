package com.paraskcd.influentiallauncher.controlcenter.infrastructure.shell

object ShellCommands {
    const val Wifi = "cmd wifi set-wifi-enabled %s"
    const val Bluetooth = "cmd bluetooth_manager %s"
    const val Airplane = "cmd connectivity airplane-mode %s"
    const val MobileData = "svc data %s"
    const val DoNotDisturb = "cmd notification set_dnd %s"
    const val BatterySaver = "cmd power set-mode %s"
    const val NightLight = "settings put secure night_display_activated %s"
    const val DarkMode = "cmd uimode night %s"
    const val AutoRotate = "settings put system accelerometer_rotation %s"
    const val Location = "cmd location set-location-enabled %s"
    const val GrantSecureSettings = "pm grant %s android.permission.WRITE_SECURE_SETTINGS"
    const val AllowWriteSettings = "appops set %s WRITE_SETTINGS allow"

    object Values {
        const val Enabled = "enabled"
        const val Disabled = "disabled"
        const val Enable = "enable"
        const val Disable = "disable"
        const val On = "on"
        const val Off = "off"
        const val Yes = "yes"
        const val No = "no"
        const val One = "1"
        const val Zero = "0"
        const val True = "true"
        const val False = "false"
    }
}
