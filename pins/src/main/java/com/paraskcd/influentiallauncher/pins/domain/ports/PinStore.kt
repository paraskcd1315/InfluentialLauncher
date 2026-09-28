// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.pins.domain.ports

import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.pins.domain.model.PinTarget
import kotlinx.coroutines.flow.Flow

interface PinStore {
    fun pins(target: PinTarget): Flow<List<AppId>>

    suspend fun save(target: PinTarget, pins: List<AppId>)
}
