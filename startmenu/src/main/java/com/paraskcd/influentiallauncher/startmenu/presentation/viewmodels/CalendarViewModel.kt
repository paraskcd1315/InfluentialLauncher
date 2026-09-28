// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.calendar.domain.model.CalendarEvent
import com.paraskcd.influentiallauncher.calendar.domain.ports.CalendarSource
import com.paraskcd.influentiallauncher.startmenu.presentation.model.PermissionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarSource: CalendarSource
) : ViewModel() {

    val permission: String = calendarSource.permission

    private val _permissionState = MutableStateFlow(currentPermission())
    val permissionState: StateFlow<PermissionState> = _permissionState.asStateFlow()

    private val _day = MutableStateFlow(LocalDate.now())
    val day: StateFlow<LocalDate> = _day.asStateFlow()

    val events: StateFlow<List<CalendarEvent>?> = combine(_permissionState, _day) { state, day -> state to day }
        .flatMapLatest { (state, day) ->
            if (state == PermissionState.Granted) {
                calendarSource.events(day).map<List<CalendarEvent>, List<CalendarEvent>?> { it }.onStart { emit(null) }
            } else {
                emptyFlow()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), null)

    fun previousDay() {
        _day.value = _day.value.minusDays(1)
    }

    fun nextDay() {
        _day.value = _day.value.plusDays(1)
    }

    fun setDay(date: LocalDate) {
        _day.value = date
    }

    fun today() {
        _day.value = LocalDate.now()
    }

    fun refreshPermission() {
        _permissionState.value = currentPermission()
    }

    fun open(event: CalendarEvent) {
        calendarSource.open(event)
    }

    private fun currentPermission(): PermissionState =
        if (calendarSource.hasPermission()) PermissionState.Granted else PermissionState.Missing

    private companion object {
        const val StopTimeoutMs = 5_000L
    }
}
