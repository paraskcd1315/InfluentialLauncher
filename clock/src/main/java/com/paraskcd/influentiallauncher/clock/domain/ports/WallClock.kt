package com.paraskcd.influentiallauncher.clock.domain.ports

import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface WallClock {
    val now: Flow<ZonedDateTime>
}
