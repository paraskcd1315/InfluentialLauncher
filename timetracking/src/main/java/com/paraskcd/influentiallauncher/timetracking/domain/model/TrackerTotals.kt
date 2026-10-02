// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.domain.model

import java.time.Duration
import java.time.Instant
import java.time.ZoneId

data class TrackerTotals(val today: Duration, val week: Duration) {
    companion object {
        fun of(week: List<TimeEntry>, running: TimeEntry?, now: Instant, zone: ZoneId): TrackerTotals {
            val entries = if (running == null || week.any { it.id == running.id }) week else week + running
            val today = now.atZone(zone).toLocalDate()
            return TrackerTotals(
                today = entries.filter { it.start.atZone(zone).toLocalDate() == today }.elapsed(now),
                week = entries.elapsed(now)
            )
        }

        private fun List<TimeEntry>.elapsed(now: Instant): Duration = fold(Duration.ZERO) { total, entry ->
            total + Duration.between(entry.start, entry.end ?: now).coerceAtLeast(Duration.ZERO)
        }
    }
}
