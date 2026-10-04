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
import com.paraskcd.influentiallauncher.weather.domain.ports.WeatherSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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
    private val tracking: TimeTracking
) : ViewModel() {

    val locationPermission: String = weather.permission

    private val refresh = MutableStateFlow(0)
    private val timerRefresh = MutableStateFlow(0)

    private val nowPlaying = refresh.flatMapLatest { if (media.hasAccess()) media.nowPlaying() else flowOf(null) }

    private var lastWeather: WeatherLoad = WeatherLoad.Loading
    private var lastTimers: Map<Tracker, GlanceCard.Timer> = emptyMap()

    private val forecast = refresh.flatMapLatest {
        flow<WeatherLoad> {
            while (true) {
                val now = runCatching { weather.forecast()?.now }.onFailure { Log.w(LogTag, "weather failed", it) }.getOrNull()
                emit(WeatherLoad.Ready(now).also { lastWeather = it })
                delay(WeatherPollMs)
            }
        }
    }.onStart { emit(lastWeather) }

    private val timers = timerRefresh.flatMapLatest {
        channelFlow {
            if (!tracking.available) return@channelFlow
            launch { tracking.changes.collect { tracker -> readTimer(tracker) { send(it) } } }
            while (true) {
                coroutineScope { Tracker.entries.forEach { tracker -> launch { readTimer(tracker) { send(it) } } } }
                delay(TimerPollMs)
            }
        }
    }.onStart { emit(shownTimers()) }

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
        viewModelScope.launch {
            tracking.takeSchedule()
            timerRefresh.value += 1
        }
    }

    fun requestMediaAccess() = media.requestAccess()

    fun playPause() = media.playPause()

    fun next() = media.next()

    fun previous() = media.previous()

    fun openMedia() = media.open()

    fun stop(entry: TimeEntry) {
        viewModelScope.launch {
            runCatching { tracking.stop(entry) }.onFailure { Log.w(LogTag, "stop failed", it) }
        }
    }

    private suspend fun readTimer(tracker: Tracker, publish: suspend (List<GlanceCard.Timer>) -> Unit) {
        val held = lastTimers[tracker] ?: GlanceCard.Timer(tracker, entry = null, week = null, weekTarget = null)
        val entry = runCatching { tracking.running(tracker) }
            .onFailure { Log.w(LogTag, "reading running $tracker failed: ${it.message}") }
            .getOrElse { held.entry }
        lastTimers = lastTimers + (tracker to held.copy(entry = entry))
        publish(shownTimers())
        if (entry == null && !GlanceOrder.showsIdle(tracker)) return
        if (!tracking.credentials.first().configured(tracker)) return
        val week = runCatching { tracking.week(tracker) }
            .onFailure { Log.w(LogTag, "reading $tracker week failed: ${it.message}") }
            .getOrNull() ?: return
        val target = runCatching { tracking.weekTarget(tracker) }.getOrNull()
        lastTimers = lastTimers + (tracker to lastTimers.getValue(tracker).copy(week = week, weekTarget = target))
        publish(shownTimers())
    }

    private fun shownTimers(): List<GlanceCard.Timer> = Tracker.entries.mapNotNull { lastTimers[it] }

    private companion object {
        const val LogTag = "Glance"
        const val StopTimeoutMs = 5_000L
        const val WeatherPollMs = 30 * 60 * 1000L
        const val TimerPollMs = 30_000L
    }
}
