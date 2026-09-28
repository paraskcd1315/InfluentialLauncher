// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.utils

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object TimelineLayout {

    data class Placed<T>(val item: T, val startMinute: Int, val endMinute: Int, val lane: Int, val lanes: Int)

    private data class Span<T>(val item: T, val from: Int, val to: Int, val shownTo: Int)

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
            val to = minutesBetween(dayStart, end(item) ?: now).coerceIn(from, dayMinutes)
            Span(item, from, to, maxOf(to, from + TimelineMetrics.minimumBlockMinutes).coerceAtMost(dayMinutes))
        }.sortedBy { it.from }
        val placed = mutableListOf<Placed<T>>()
        val group = mutableListOf<Pair<Span<T>, Int>>()
        val laneEnds = mutableListOf<Int>()
        var groupEnd = Int.MIN_VALUE
        fun closeGroup() {
            val lanes = laneEnds.size.coerceAtLeast(1)
            group.forEach { (span, lane) -> placed += Placed(span.item, span.from, span.shownTo, lane, lanes) }
            group.clear()
            laneEnds.clear()
        }
        spans.forEach { span ->
            if (group.isNotEmpty() && span.from >= groupEnd) closeGroup()
            val free = laneEnds.indexOfFirst { it <= span.from }
            val lane = if (free >= 0) free else laneEnds.size.also { laneEnds.add(0) }
            laneEnds[lane] = span.to
            group += span to lane
            groupEnd = if (group.size == 1) span.to else maxOf(groupEnd, span.to)
        }
        closeGroup()
        return placed
    }

    fun minutesBetween(from: Instant, to: Instant): Int = Duration.between(from, to).toMinutes().toInt()

    fun snap(minutes: Int): Int = Math.round(minutes / TimelineMetrics.snapMinutes.toFloat()) * TimelineMetrics.snapMinutes
}
