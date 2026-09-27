package com.paraskcd.influentiallauncher.controlcenter.domain.ports

import com.paraskcd.influentiallauncher.controlcenter.domain.model.ControlState
import com.paraskcd.influentiallauncher.controlcenter.domain.model.QuickToggle
import kotlinx.coroutines.flow.Flow

interface SystemControls {
    val state: Flow<ControlState>

    suspend fun set(toggle: QuickToggle, on: Boolean)

    fun setBrightness(level: Float)

    fun setAutoBrightness(on: Boolean)

    fun setVolume(level: Float)

    fun requestAccess()

    fun openDetails(toggle: QuickToggle)

    fun openVolumePanel()

    fun openSettings()
}
