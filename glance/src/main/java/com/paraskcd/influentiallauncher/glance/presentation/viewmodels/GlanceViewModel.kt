// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.glance.presentation.model.GlanceCard
import com.paraskcd.influentiallauncher.glance.presentation.model.WeatherLoad
import com.paraskcd.influentiallauncher.glance.presentation.utils.GlanceOrder
import com.paraskcd.influentiallauncher.media.domain.ports.MediaSource
import com.paraskcd.influentiallauncher.timetracking.domain.model.TimeEntry
import com.paraskcd.influentiallauncher.timetracking.domain.model.Tracker
import com.paraskcd.influentiallauncher.timetracking.domain.usecase.TimeTracking
import com.paraskcd.influentiallauncher.weather.domain.ports.SavedPlaces
import com.paraskcd.influentiallauncher.weather.domain.ports.WeatherSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GlanceViewModel @Inject constructor(
    private val media: MediaSource,
    private val weather: WeatherSource,
    private val tracking: TimeTracking,
    savedPlaces: SavedPlaces
) : ViewModel() {

    val locationPermission: String = weather.permission

    private val refresh = MutableStateFlow(0)
    private val timerRefresh = MutableStateFlow(0)

    private val nowPlaying = refresh.flatMapLatest { if (media.hasAccess()) media.nowPlaying() else flowOf(null) }

    private var lastWeather: WeatherLoad = WeatherLoad.Loading
    private var lastTimers: List<TimeEntry> = emptyList()

    private val forecast = combine(refresh, savedPlaces.selected) { tick, place -> tick to place?.key() }.flatMapLatest {
        flow<WeatherLoad> {
            while (true) {
                val now = runCatching { weather.forecast()?.now }.onFailure { Log.w(LogTag, "weather failed", it) }.getOrNull()
                emit(WeatherLoad.Ready(now).also { lastWeather = it })
                delay(WeatherPollMs)
            }
        }
    }.onStart { emit(lastWeather) }

    private val timers = timerRefresh.flatMapLatest {
        flow {
            while (true) {
                emit(Tracker.entries.mapNotNull { tracker -> runCatching { tracking.running(tracker) }.getOrNull() }.also { lastTimers = it })
                delay(TimerPollMs)
            }
        }
    }.onStart { emit(lastTimers) }

    val cards: StateFlow<List<GlanceCard>> = combine(nowPlaying, timers, forecast, refresh) { playing, running, current, _ ->
        GlanceOrder.of(
            media = playing,
            mediaAccess = media.hasAccess(),
            timers = running,
            weather = current,
            locationAccess = weather.hasPermission()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), listOf(GlanceCard.Loading))

    fun refresh() {
        refresh.value += 1
        timerRefresh.value += 1
    }

    fun requestMediaAccess() = media.requestAccess()

    fun playPause() = media.playPause()

    fun next() = media.next()

    fun previous() = media.previous()

    fun openMedia() = media.open()

    fun stop(entry: TimeEntry) {
        viewModelScope.launch {
            runCatching { tracking.stop(entry) }.onFailure { Log.w(LogTag, "stop failed", it) }
            timerRefresh.value += 1
        }
    }

    private companion object {
        const val LogTag = "Glance"
        const val StopTimeoutMs = 5_000L
        const val WeatherPollMs = 30 * 60 * 1000L
        const val TimerPollMs = 30_000L
    }
}
