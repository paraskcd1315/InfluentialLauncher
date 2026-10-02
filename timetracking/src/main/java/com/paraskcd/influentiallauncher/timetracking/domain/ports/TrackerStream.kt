// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.domain.ports

import com.paraskcd.influentiallauncher.timetracking.domain.model.StreamSignal
import com.paraskcd.influentiallauncher.timetracking.domain.model.Tracker
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerCredentials
import kotlinx.coroutines.flow.Flow

interface TrackerStream {
    val tracker: Tracker

    fun signals(credentials: TrackerCredentials): Flow<StreamSignal>
}
