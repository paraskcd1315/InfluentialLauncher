// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.devicestatus.domain.ports

import com.paraskcd.influentiallauncher.devicestatus.domain.model.BatteryStatus
import com.paraskcd.influentiallauncher.devicestatus.domain.model.CellularStatus
import com.paraskcd.influentiallauncher.devicestatus.domain.model.WifiStatus
import kotlinx.coroutines.flow.Flow

interface DeviceStatusSource {
    val battery: Flow<BatteryStatus>
    val wifi: Flow<WifiStatus>
    val cellular: Flow<CellularStatus>
}
