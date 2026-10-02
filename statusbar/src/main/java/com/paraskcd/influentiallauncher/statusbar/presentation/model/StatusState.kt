// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.statusbar.presentation.model

import com.paraskcd.influentiallauncher.devicestatus.domain.model.BatteryStatus
import com.paraskcd.influentiallauncher.devicestatus.domain.model.CellularStatus
import com.paraskcd.influentiallauncher.devicestatus.domain.model.MobileDataType
import com.paraskcd.influentiallauncher.devicestatus.domain.model.WifiStatus

data class StatusState(
    val battery: BatteryStatus,
    val wifi: WifiStatus,
    val cellular: CellularStatus,
    val vpn: Boolean
) {
    val mobileData: MobileDataType?
        get() = cellular.dataType.takeUnless { wifi.connected }
}
