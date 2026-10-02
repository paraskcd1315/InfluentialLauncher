// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.statusbar.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.devicestatus.domain.ports.DeviceStatusSource
import com.paraskcd.influentiallauncher.statusbar.presentation.model.StatusState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StatusBarViewModel @Inject constructor(private val deviceStatus: DeviceStatusSource) : ViewModel() {
    val status: StateFlow<StatusState?> = combine(
        deviceStatus.battery,
        deviceStatus.wifi,
        deviceStatus.cellular,
        deviceStatus.vpn
    ) { battery, wifi, cellular, vpn ->
        StatusState(battery, wifi, cellular, vpn)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), null)

    fun refresh() = deviceStatus.refresh()

    private companion object {
        const val StopTimeoutMs = 5_000L
    }
}
