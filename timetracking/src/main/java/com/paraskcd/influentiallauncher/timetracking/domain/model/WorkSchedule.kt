// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.domain.model

import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import kotlin.math.roundToLong

data class WorkSchedule(
    val weeklyHours: Double = DefaultWeeklyHours,
    val hoursByMonth: Map<Int, Double> = emptyMap(),
    val weeklyDaysOff: Set<DayOfWeek> = DefaultWeeklyDaysOff,
    val holidays: Set<LocalDate> = emptySet()
) {
    val workdays: Int get() = DaysInWeek - weeklyDaysOff.size

    fun isDayOff(day: LocalDate): Boolean = day.dayOfWeek in weeklyDaysOff || day in holidays

    fun target(weekStart: LocalDate): Duration {
        if (workdays <= 0) return Duration.ZERO
        val seconds = (0 until DaysInWeek).sumOf { offset ->
            val day = weekStart.plusDays(offset.toLong())
            if (isDayOff(day)) 0L else ((hoursByMonth[day.monthValue] ?: weeklyHours) / workdays * SecondsPerHour).roundToLong()
        }
        return Duration.ofSeconds(seconds)
    }

    companion object {
        const val DefaultWeeklyHours = 40.0
        val DefaultWeeklyDaysOff = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        private const val DaysInWeek = 7
        private const val SecondsPerHour = 3600

        fun daysOffAfter(workdays: Int): Set<DayOfWeek> = DayOfWeek.entries.filter { it.value > workdays }.toSet()
    }
}
