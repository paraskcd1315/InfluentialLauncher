// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.domain.ports

import com.paraskcd.influentiallauncher.timetracking.domain.model.StartTimer
import com.paraskcd.influentiallauncher.timetracking.domain.model.TimeEntry
import com.paraskcd.influentiallauncher.timetracking.domain.model.Tracker
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerActivity
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerCredentials
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerProject
import java.time.Instant

interface TrackerClient {
    val tracker: Tracker

    suspend fun entries(credentials: TrackerCredentials, from: Instant, to: Instant): List<TimeEntry>

    suspend fun running(credentials: TrackerCredentials): TimeEntry?

    suspend fun projects(credentials: TrackerCredentials): List<TrackerProject>

    suspend fun activities(credentials: TrackerCredentials, projectId: String?): List<TrackerActivity>

    suspend fun start(credentials: TrackerCredentials, timer: StartTimer): TimeEntry?

    suspend fun stop(credentials: TrackerCredentials, entry: TimeEntry): TimeEntry?

    suspend fun move(credentials: TrackerCredentials, entry: TimeEntry, start: Instant, end: Instant?): TimeEntry?
}
