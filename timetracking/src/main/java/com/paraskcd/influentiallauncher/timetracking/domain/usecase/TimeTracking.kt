// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.domain.usecase

import com.paraskcd.influentiallauncher.timetracking.BuildConfig
import com.paraskcd.influentiallauncher.timetracking.domain.model.RunningUpdate
import com.paraskcd.influentiallauncher.timetracking.domain.model.StartTimer
import com.paraskcd.influentiallauncher.timetracking.domain.model.StreamSignal
import com.paraskcd.influentiallauncher.timetracking.domain.model.TimeEntry
import com.paraskcd.influentiallauncher.timetracking.domain.model.Tracker
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerActivity
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerCredentials
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerProject
import com.paraskcd.influentiallauncher.timetracking.domain.model.WorkSchedule
import com.paraskcd.influentiallauncher.timetracking.domain.ports.CredentialsStore
import com.paraskcd.influentiallauncher.timetracking.domain.ports.ScheduleInbox
import com.paraskcd.influentiallauncher.timetracking.domain.ports.ScheduleCodec
import com.paraskcd.influentiallauncher.timetracking.domain.ports.TrackerClient
import com.paraskcd.influentiallauncher.timetracking.domain.ports.TrackerStream
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@Singleton
class TimeTracking @Inject constructor(
    private val store: CredentialsStore,
    private val scheduleCodec: ScheduleCodec,
    private val scheduleInbox: ScheduleInbox,
    clients: Set<@JvmSuppressWildcards TrackerClient>,
    streams: Set<@JvmSuppressWildcards TrackerStream>
) {
    private val byTracker = clients.associateBy { it.tracker }
    private val streamByTracker = streams.associateBy { it.tracker }
    private val caches = Tracker.entries.associateWith { TrackerCache(hasStream = it in streamByTracker) }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val writes = MutableSharedFlow<Tracker>(extraBufferCapacity = WriteBuffer)
    private val contracted = setOf(Tracker.Toggl)
    private val alwaysShown = setOf(Tracker.Toggl)
    private val reachable = MutableStateFlow<Set<Tracker>>(emptySet())
    private var seen: TrackerCredentials? = null

    val available: Boolean = BuildConfig.PERSONAL_EDITION

    val credentials: Flow<TrackerCredentials> = store.credentials

    val shown: Flow<List<Tracker>> = combine(store.credentials, reachable) { held, answered ->
        if (!available) emptyList()
        else Tracker.entries.filter { held.configured(it) && (it in alwaysShown || it in answered) }
    }.distinctUntilChanged()

    val schedule: Flow<WorkSchedule?> = store.credentials
        .map { it.workSchedule }
        .distinctUntilChanged()
        .map(scheduleCodec::read)

    val changes: SharedFlow<Tracker> = merge(streamChanges(), writes)
        .shareIn(scope, SharingStarted.WhileSubscribed(StreamLingerMs), replay = 0)

    init {
        scope.launch { takeSchedule() }
    }

    suspend fun takeSchedule() {
        val text = scheduleInbox.take() ?: return
        if (scheduleCodec.read(text) == null) return
        store.update { it.copy(workSchedule = text) }
    }

    suspend fun updateSchedule(transform: (WorkSchedule) -> WorkSchedule) {
        store.update { held ->
            val next = transform(scheduleCodec.read(held.workSchedule) ?: WorkSchedule())
            held.copy(workSchedule = scheduleCodec.write(next))
        }
    }

    suspend fun day(tracker: Tracker, day: LocalDate): List<TimeEntry> {
        val zone = ZoneId.systemDefault()
        val monday = weekStart(zone)
        if (!day.isBefore(monday) && day.isBefore(monday.plusWeeks(1))) {
            return week(tracker).filter { it.start.atZone(zone).toLocalDate() == day }
        }
        return span(tracker, DayKey + day, day, day.plusDays(1), zone)
    }

    suspend fun week(tracker: Tracker): List<TimeEntry> {
        val zone = ZoneId.systemDefault()
        val monday = weekStart(zone)
        return span(tracker, WeekKey + monday, monday, monday.plusWeeks(1), zone)
    }

    suspend fun weekTarget(tracker: Tracker): Duration? {
        if (tracker !in contracted) return null
        val held = scheduleCodec.read(store.credentials.first().workSchedule) ?: return null
        return held.target(weekStart(ZoneId.systemDefault()))
    }

    private suspend fun span(tracker: Tracker, key: String, from: LocalDate, to: LocalDate, zone: ZoneId): List<TimeEntry> =
        withClient(tracker, emptyList()) { client, credentials ->
            caches.getValue(tracker).answer(key) {
                client.entries(credentials, from.atStartOfDay(zone).toInstant(), to.atStartOfDay(zone).toInstant())
                    .sortedBy { it.start }
            }
        }

    private fun weekStart(zone: ZoneId): LocalDate =
        LocalDate.now(zone).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    suspend fun running(tracker: Tracker): TimeEntry? = withClient(tracker, null) { client, credentials ->
        caches.getValue(tracker).answer(TrackerCache.RunningKey) { client.running(credentials) }
    }

    suspend fun projects(tracker: Tracker): List<TrackerProject> =
        withClient(tracker, emptyList()) { client, credentials -> client.projects(credentials) }

    suspend fun activities(tracker: Tracker, projectId: String?): List<TrackerActivity> =
        withClient(tracker, emptyList()) { client, credentials -> client.activities(credentials, projectId) }

    suspend fun start(tracker: Tracker, timer: StartTimer) {
        val started = withClient(tracker, null) { client, credentials -> client.start(credentials, timer) }
        wrote(tracker, started?.let { RunningUpdate.Started(it) })
    }

    suspend fun stop(entry: TimeEntry) {
        withClient(entry.tracker, null) { client, credentials -> client.stop(credentials, entry) }
        wrote(entry.tracker, RunningUpdate.Ended(entry.id))
    }

    suspend fun move(entry: TimeEntry, start: Instant, end: Instant?) {
        val moved = withClient(entry.tracker, null) { client, credentials -> client.move(credentials, entry, start, end) }
        wrote(entry.tracker, moved?.takeIf { it.running }?.let { RunningUpdate.Started(it) })
    }

    private fun streamChanges(): Flow<Tracker> = store.credentials.map { it.access }.distinctUntilChanged().flatMapLatest { credentials ->
        streamByTracker.values
            .filter { credentials.configured(it.tracker) }
            .map { stream ->
                val cache = caches.getValue(stream.tracker)
                stream.signals(credentials)
                    .map { signal -> withProject(stream.tracker, credentials, signal) }
                    .filter { cache.signal(it) }
                    .onCompletion { cache.signal(StreamSignal.Closed) }
                    .map { stream.tracker }
                    .debounce(SettleMs)
            }
            .merge()
    }

    private suspend fun withProject(tracker: Tracker, credentials: TrackerCredentials, signal: StreamSignal): StreamSignal {
        val started = (signal as? StreamSignal.Changed)?.update as? RunningUpdate.Started ?: return signal
        val projectId = started.entry.projectId ?: return signal
        val client = byTracker[tracker] ?: return signal
        val project = runCatching { caches.getValue(tracker).answer(ProjectsKey) { client.projects(credentials) } }
            .getOrDefault(emptyList())
            .firstOrNull { it.id == projectId } ?: return signal
        val entry = started.entry.copy(projectName = project.name, colourArgb = project.colourArgb, clientName = project.clientName)
        return StreamSignal.Changed(RunningUpdate.Started(entry))
    }

    private suspend fun wrote(tracker: Tracker, update: RunningUpdate?) {
        caches.getValue(tracker).wrote(update)
        writes.emit(tracker)
    }

    private suspend fun <T> withClient(
        tracker: Tracker,
        unconfigured: T,
        block: suspend (TrackerClient, TrackerCredentials) -> T
    ): T {
        val credentials = store.credentials.first().access
        if (seen != credentials) {
            if (seen != null) caches.values.forEach { it.clear() }
            seen = credentials
        }
        if (!credentials.configured(tracker)) return unconfigured
        val client = byTracker[tracker] ?: return unconfigured
        return try {
            block(client, credentials).also { reachable.update { it + tracker } }
        } catch (error: IOException) {
            reachable.update { it - tracker }
            throw error
        }
    }

    suspend fun preferred(date: LocalDate, shown: List<Tracker>): Tracker? {
        if (shown.size < 2) return shown.firstOrNull()
        val held = scheduleCodec.read(store.credentials.first().workSchedule) ?: WorkSchedule()
        val order = if (held.isDayOff(date)) shown.sortedBy { it in contracted } else shown.sortedBy { it !in contracted }
        if (date == LocalDate.now()) {
            order.firstOrNull { quietly(null) { running(it) } != null }?.let { return it }
        }
        return order.firstOrNull { quietly(emptyList()) { day(it, date) }.isNotEmpty() } ?: order.first()
    }

    private suspend fun <T> quietly(fallback: T, read: suspend () -> T): T =
        runCatching { read() }.onFailure { if (it is CancellationException) throw it }.getOrDefault(fallback)

    suspend fun probe() {
        val credentials = store.credentials.first()
        Tracker.entries
            .filter { it !in alwaysShown && credentials.configured(it) }
            .forEach { tracker -> quietly(null) { running(tracker) } }
    }

    private companion object {
        const val ProjectsKey = "projects"
        const val DayKey = "day:"
        const val WeekKey = "week:"
        const val WriteBuffer = 8
        const val SettleMs = 900L
        const val StreamLingerMs = 5_000L
    }
}
