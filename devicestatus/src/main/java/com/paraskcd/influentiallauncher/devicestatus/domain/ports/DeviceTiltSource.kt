// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.devicestatus.domain.ports

import com.paraskcd.influentiallauncher.devicestatus.domain.model.Tilt
import kotlinx.coroutines.flow.Flow

interface DeviceTiltSource {
    val tilt: Flow<Tilt>
}
