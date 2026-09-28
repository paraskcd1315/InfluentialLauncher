// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.clock.domain.ports

import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface WallClock {
    val now: Flow<ZonedDateTime>
}
