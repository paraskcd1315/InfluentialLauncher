// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.domain.model

import java.time.Duration
import java.time.LocalDate
import kotlin.math.roundToLong

data class WorkSchedule(
    val weeklyHours: Double = DefaultWeeklyHours,
    val hoursByMonth: Map<Int, Double> = emptyMap(),
    val workdays: Int = DefaultWorkdays,
    val holidays: Set<LocalDate> = emptySet()
) {
    fun target(weekStart: LocalDate): Duration {
        if (workdays <= 0) return Duration.ZERO
        val seconds = (0 until DaysInWeek).sumOf { offset ->
            val day = weekStart.plusDays(offset.toLong())
            if (offset >= workdays || day in holidays) {
                0L
            } else {
                ((hoursByMonth[day.monthValue] ?: weeklyHours) / workdays * SecondsPerHour).roundToLong()
            }
        }
        return Duration.ofSeconds(seconds)
    }

    companion object {
        const val DefaultWeeklyHours = 40.0
        const val DefaultWorkdays = 5
        private const val DaysInWeek = 7
        private const val SecondsPerHour = 3600
    }
}
