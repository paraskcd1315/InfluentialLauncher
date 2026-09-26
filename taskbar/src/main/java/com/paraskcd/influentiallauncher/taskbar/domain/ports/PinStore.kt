package com.paraskcd.influentiallauncher.taskbar.domain.ports

import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import kotlinx.coroutines.flow.Flow

interface PinStore {
    val pins: Flow<List<AppId>>

    suspend fun save(pins: List<AppId>)
}
