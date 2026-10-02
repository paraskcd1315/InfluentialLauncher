// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.domain.usecase

import com.paraskcd.influentiallauncher.timetracking.domain.model.RunningUpdate
import com.paraskcd.influentiallauncher.timetracking.domain.model.StartTimer
import com.paraskcd.influentiallauncher.timetracking.domain.model.StreamSignal
import com.paraskcd.influentiallauncher.timetracking.domain.model.TimeEntry
import com.paraskcd.influentiallauncher.timetracking.domain.model.Tracker
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerActivity
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerCredentials
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerProject
import com.paraskcd.influentiallauncher.timetracking.domain.ports.CredentialsStore
import com.paraskcd.influentiallauncher.timetracking.domain.ports.TrackerClient
import com.paraskcd.influentiallauncher.timetracking.domain.ports.TrackerStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@Singleton
class TimeTracking @Inject constructor(
    private val store: CredentialsStore,
    clients: Set<@JvmSuppressWildcards TrackerClient>,
    streams: Set<@JvmSuppressWildcards TrackerStream>
) {
    private val byTracker = clients.associateBy { it.tracker }
    private val streamByTracker = streams.associateBy { it.tracker }
    private val caches = Tracker.entries.associateWith { TrackerCache(hasStream = it in streamByTracker) }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val writes = MutableSharedFlow<Tracker>(extraBufferCapacity = WriteBuffer)
    private var seen: TrackerCredentials? = null

    val credentials: Flow<TrackerCredentials> = store.credentials

    val changes: SharedFlow<Tracker> = merge(streamChanges(), writes)
        .shareIn(scope, SharingStarted.WhileSubscribed(StreamLingerMs), replay = 0)

    suspend fun updateCredentials(transform: (TrackerCredentials) -> TrackerCredentials) = store.update(transform)

    suspend fun day(tracker: Tracker, day: LocalDate): List<TimeEntry> = withClient(tracker, emptyList()) { client, credentials ->
        caches.getValue(tracker).answer(DayKey + day) {
            val zone = ZoneId.systemDefault()
            client.entries(credentials, day.atStartOfDay(zone).toInstant(), day.plusDays(1).atStartOfDay(zone).toInstant())
                .sortedBy { it.start }
        }
    }

    suspend fun running(tracker: Tracker): TimeEntry? = withClient(tracker, null) { client, credentials ->
        caches.getValue(tracker).answer(TrackerCache.RunningKey) { client.running(credentials) }
    }

    suspend fun projects(tracker: Tracker): List<TrackerProject> =
        withClient(tracker, emptyList()) { client, credentials -> client.projects(credentials) }

    suspend fun activities(tracker: Tracker, projectId: String?): List<TrackerActivity> =
        withClient(tracker, emptyList()) { client, credentials -> client.activities(credentials, projectId) }

    suspend fun start(tracker: Tracker, timer: StartTimer) {
        withClient(tracker, Unit) { client, credentials -> client.start(credentials, timer) }
        wrote(tracker)
    }

    suspend fun stop(entry: TimeEntry) {
        withClient(entry.tracker, Unit) { client, credentials -> client.stop(credentials, entry) }
        wrote(entry.tracker)
    }

    suspend fun move(entry: TimeEntry, start: Instant, end: Instant?) {
        withClient(entry.tracker, Unit) { client, credentials -> client.move(credentials, entry, start, end) }
        wrote(entry.tracker)
    }

    private fun streamChanges(): Flow<Tracker> = store.credentials.distinctUntilChanged().flatMapLatest { credentials ->
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

    private suspend fun wrote(tracker: Tracker) {
        caches.getValue(tracker).wrote()
        writes.emit(tracker)
    }

    private suspend fun <T> withClient(
        tracker: Tracker,
        unconfigured: T,
        block: suspend (TrackerClient, TrackerCredentials) -> T
    ): T {
        val credentials = store.credentials.first()
        if (seen != credentials) {
            if (seen != null) caches.values.forEach { it.clear() }
            seen = credentials
        }
        if (!credentials.configured(tracker)) return unconfigured
        val client = byTracker[tracker] ?: return unconfigured
        return block(client, credentials)
    }

    private companion object {
        const val ProjectsKey = "projects"
        const val DayKey = "day:"
        const val WriteBuffer = 8
        const val SettleMs = 1_500L
        const val StreamLingerMs = 5_000L
    }
}
