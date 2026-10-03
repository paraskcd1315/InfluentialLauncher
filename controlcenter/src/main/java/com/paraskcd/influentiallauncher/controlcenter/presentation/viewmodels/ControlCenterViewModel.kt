// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.controlcenter.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.controlcenter.domain.model.ControlState
import com.paraskcd.influentiallauncher.controlcenter.domain.model.QuickToggle
import com.paraskcd.influentiallauncher.controlcenter.domain.ports.SystemControls
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ControlCenterViewModel @Inject constructor(
    private val controls: SystemControls
) : ViewModel() {

    private val overrides = MutableStateFlow<Map<QuickToggle, Boolean>>(emptyMap())
    private val brightnessDrag = MutableStateFlow<Float?>(null)
    private val volumeDrag = MutableStateFlow<Float?>(null)
    private var brightnessSettle: Job? = null
    private var volumeSettle: Job? = null

    val state: StateFlow<ControlState?> = combine(controls.state, overrides, brightnessDrag, volumeDrag) { real, pending, brightness, volume ->
        real.copy(
            toggles = real.toggles + pending,
            brightness = brightness ?: real.brightness,
            volume = volume ?: real.volume
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), null)

    fun toggle(toggle: QuickToggle) {
        val current = state.value ?: return
        if (toggle.needsShell && !current.ready) {
            controls.requestAccess()
            return
        }
        val target = current.toggles[toggle] != true
        overrides.update { it + (toggle to target) }
        viewModelScope.launch {
            controls.set(toggle, target)
            delay(SettleMs)
            overrides.update { it - toggle }
        }
    }

    fun setBrightness(level: Float) {
        brightnessSettle?.cancel()
        brightnessDrag.value = level
        controls.setBrightness(level)
    }

    fun brightnessDone() {
        brightnessSettle = viewModelScope.launch {
            delay(SettleMs)
            brightnessDrag.value = null
        }
    }

    fun toggleAutoBrightness() {
        val current = state.value ?: return
        controls.setAutoBrightness(!current.autoBrightness)
    }

    fun setVolume(level: Float) {
        volumeSettle?.cancel()
        volumeDrag.value = level
        controls.setVolume(level)
    }

    fun volumeDone() {
        volumeSettle = viewModelScope.launch {
            delay(SettleMs)
            volumeDrag.value = null
        }
    }

    fun requestAccess() = controls.requestAccess()

    fun openDetails(toggle: QuickToggle) = controls.openDetails(toggle)

    fun openVolumePanel() = controls.openVolumePanel()

    fun openSettings() = controls.openSettings()

    private companion object {
        const val StopTimeoutMs = 5_000L
        const val SettleMs = 1_500L
    }
}
