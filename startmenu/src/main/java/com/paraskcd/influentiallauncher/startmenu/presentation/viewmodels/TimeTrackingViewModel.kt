// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.startmenu.presentation.model.TrackerDay
import com.paraskcd.influentiallauncher.timetracking.domain.model.StartTimer
import com.paraskcd.influentiallauncher.timetracking.domain.model.TimeEntry
import com.paraskcd.influentiallauncher.timetracking.domain.model.Tracker
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerActivity
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerCredentials
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerProject
import com.paraskcd.influentiallauncher.timetracking.domain.usecase.TimeTracking
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TimeTrackingViewModel @Inject constructor(
    private val tracking: TimeTracking
) : ViewModel() {

    val credentials: StateFlow<TrackerCredentials> = tracking.credentials
        .stateIn(viewModelScope, SharingStarted.Eagerly, TrackerCredentials())

    private val _tracker = MutableStateFlow(Tracker.Toggl)
    val tracker: StateFlow<Tracker> = _tracker.asStateFlow()

    private val _day = MutableStateFlow<TrackerDay?>(null)
    val day: StateFlow<TrackerDay?> = _day.asStateFlow()

    private val _running = MutableStateFlow<Map<Tracker, TimeEntry?>>(emptyMap())
    val running: StateFlow<Map<Tracker, TimeEntry?>> = _running.asStateFlow()

    private var shownDay: LocalDate = LocalDate.now()

    fun selectTracker(tracker: Tracker) {
        _tracker.value = tracker
    }

    fun load(date: LocalDate) {
        shownDay = date
        val tracker = _tracker.value
        viewModelScope.launch {
            val previous = _day.value?.takeIf { it.tracker == tracker && it.date == date }
            _day.value = previous?.copy(loading = true) ?: TrackerDay(tracker, date, emptyList(), loading = true, failed = false)
            val result = runCatching { tracking.day(tracker, date) }
                .onFailure { Log.w(LogTag, "loading $tracker day failed", it) }
            if (_tracker.value != tracker || shownDay != date) return@launch
            _day.value = TrackerDay(
                tracker = tracker,
                date = date,
                entries = result.getOrDefault(previous?.entries.orEmpty()),
                loading = false,
                failed = result.isFailure
            )
        }
    }

    suspend fun follow() {
        tracking.changes.collect {
            refreshRunning()
            load(shownDay)
        }
    }

    fun refreshRunning() {
        viewModelScope.launch {
            val next = Tracker.entries.associateWith { tracker ->
                runCatching { tracking.running(tracker) }
                    .onFailure { Log.w(LogTag, "reading running $tracker failed", it) }
                    .getOrElse { _running.value[tracker] }
            }
            _running.value = next
        }
    }

    fun start(tracker: Tracker, timer: StartTimer) {
        viewModelScope.launch {
            runCatching { tracking.start(tracker, timer) }.onFailure { Log.w(LogTag, "start failed", it) }
            refreshAfterChange()
        }
    }

    fun stop(entry: TimeEntry) {
        viewModelScope.launch {
            runCatching { tracking.stop(entry) }.onFailure { Log.w(LogTag, "stop failed", it) }
            refreshAfterChange()
        }
    }

    fun move(entry: TimeEntry, start: Instant, end: Instant?) {
        val current = _day.value
        if (current != null) {
            _day.value = current.copy(entries = current.entries.map { if (it.id == entry.id) it.copy(start = start, end = end) else it })
        }
        viewModelScope.launch {
            runCatching { tracking.move(entry, start, end) }.onFailure { Log.w(LogTag, "move failed", it) }
            refreshAfterChange()
        }
    }

    suspend fun projects(tracker: Tracker): List<TrackerProject> =
        runCatching { tracking.projects(tracker) }.onFailure { Log.w(LogTag, "projects failed", it) }.getOrDefault(emptyList())

    suspend fun activities(tracker: Tracker, projectId: String?): List<TrackerActivity> =
        runCatching { tracking.activities(tracker, projectId) }.onFailure { Log.w(LogTag, "activities failed", it) }.getOrDefault(emptyList())

    fun updateCredentials(transform: (TrackerCredentials) -> TrackerCredentials) {
        viewModelScope.launch { tracking.updateCredentials(transform) }
    }

    private fun refreshAfterChange() {
        refreshRunning()
        load(shownDay)
    }

    private companion object {
        const val LogTag = "TimeTracking"
    }
}
