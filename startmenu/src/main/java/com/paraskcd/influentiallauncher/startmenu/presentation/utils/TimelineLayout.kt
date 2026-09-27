package com.paraskcd.influentiallauncher.startmenu.presentation.utils

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object TimelineLayout {

    data class Placed<T>(val item: T, val startMinute: Int, val endMinute: Int, val lane: Int, val lanes: Int)

    fun <T> place(
        items: List<T>,
        date: LocalDate,
        now: Instant,
        start: (T) -> Instant,
        end: (T) -> Instant?
    ): List<Placed<T>> {
        val zone = ZoneId.systemDefault()
        val dayStart = date.atStartOfDay(zone).toInstant()
        val dayMinutes = TimelineMetrics.hours * TimelineMetrics.minutesPerHour
        val spans = items.map { item ->
            val from = minutesBetween(dayStart, start(item)).coerceIn(0, dayMinutes)
            val to = minutesBetween(dayStart, end(item) ?: now).coerceIn(0, dayMinutes)
            Triple(item, from, maxOf(to, from + TimelineMetrics.minimumBlockMinutes).coerceAtMost(dayMinutes))
        }.sortedBy { it.second }
        val laneEnds = mutableListOf<Int>()
        val laned = spans.map { (item, from, to) ->
            val lane = laneEnds.indexOfFirst { it <= from }.takeIf { it >= 0 } ?: laneEnds.size.also { laneEnds.add(0) }
            laneEnds[lane] = to
            Placed(item, from, to, lane, 0)
        }
        val lanes = laneEnds.size.coerceAtLeast(1)
        return laned.map { it.copy(lanes = lanes) }
    }

    fun minutesBetween(from: Instant, to: Instant): Int = Duration.between(from, to).toMinutes().toInt()

    fun snap(minutes: Int): Int = Math.round(minutes / TimelineMetrics.snapMinutes.toFloat()) * TimelineMetrics.snapMinutes
}
