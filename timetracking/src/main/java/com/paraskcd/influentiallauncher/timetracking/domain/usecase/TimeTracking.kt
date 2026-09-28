// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.domain.usecase

import com.paraskcd.influentiallauncher.timetracking.domain.model.StartTimer
import com.paraskcd.influentiallauncher.timetracking.domain.model.TimeEntry
import com.paraskcd.influentiallauncher.timetracking.domain.model.Tracker
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerActivity
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerCredentials
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerProject
import com.paraskcd.influentiallauncher.timetracking.domain.ports.CredentialsStore
import com.paraskcd.influentiallauncher.timetracking.domain.ports.TrackerClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class TimeTracking @Inject constructor(
    private val store: CredentialsStore,
    clients: Set<@JvmSuppressWildcards TrackerClient>
) {
    private val byTracker = clients.associateBy { it.tracker }

    val credentials: Flow<TrackerCredentials> = store.credentials

    suspend fun updateCredentials(transform: (TrackerCredentials) -> TrackerCredentials) = store.update(transform)

    suspend fun day(tracker: Tracker, day: LocalDate): List<TimeEntry> = withClient(tracker, emptyList()) { client, credentials ->
        val zone = ZoneId.systemDefault()
        client.entries(credentials, day.atStartOfDay(zone).toInstant(), day.plusDays(1).atStartOfDay(zone).toInstant())
            .sortedBy { it.start }
    }

    suspend fun running(tracker: Tracker): TimeEntry? = withClient(tracker, null) { client, credentials -> client.running(credentials) }

    suspend fun projects(tracker: Tracker): List<TrackerProject> =
        withClient(tracker, emptyList()) { client, credentials -> client.projects(credentials) }

    suspend fun activities(tracker: Tracker, projectId: String?): List<TrackerActivity> =
        withClient(tracker, emptyList()) { client, credentials -> client.activities(credentials, projectId) }

    suspend fun start(tracker: Tracker, timer: StartTimer) =
        withClient(tracker, Unit) { client, credentials -> client.start(credentials, timer) }

    suspend fun stop(entry: TimeEntry) =
        withClient(entry.tracker, Unit) { client, credentials -> client.stop(credentials, entry) }

    suspend fun move(entry: TimeEntry, start: Instant, end: Instant?) =
        withClient(entry.tracker, Unit) { client, credentials -> client.move(credentials, entry, start, end) }

    private suspend fun <T> withClient(
        tracker: Tracker,
        unconfigured: T,
        block: suspend (TrackerClient, TrackerCredentials) -> T
    ): T {
        val credentials = store.credentials.first()
        if (!credentials.configured(tracker)) return unconfigured
        val client = byTracker[tracker] ?: return unconfigured
        return block(client, credentials)
    }
}
