// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.clock.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.clock.domain.ports.WallClock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class ClockViewModel @Inject constructor(wallClock: WallClock) : ViewModel() {
    val now: StateFlow<ZonedDateTime> = wallClock.now
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), ZonedDateTime.now())

    private companion object {
        const val StopTimeoutMs = 5_000L
    }
}
