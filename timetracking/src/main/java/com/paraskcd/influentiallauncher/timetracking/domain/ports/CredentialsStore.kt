package com.paraskcd.influentiallauncher.timetracking.domain.ports

import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerCredentials
import kotlinx.coroutines.flow.Flow

interface CredentialsStore {
    val credentials: Flow<TrackerCredentials>

    suspend fun update(transform: (TrackerCredentials) -> TrackerCredentials)
}
