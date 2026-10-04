// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.utils

import com.paraskcd.influentiallauncher.startmenu.presentation.model.DayOffRun
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object DayOffRuns {
    fun of(days: Set<LocalDate>, weeklyDaysOff: Set<DayOfWeek>): List<DayOffRun> {
        val runs = mutableListOf<MutableList<LocalDate>>()
        days.sorted().forEach { day ->
            val last = runs.lastOrNull()?.last()
            if (last != null && follows(last, day, weeklyDaysOff)) runs.last().add(day) else runs.add(mutableListOf(day))
        }
        return runs.map(::DayOffRun)
    }

    fun between(from: LocalDate, to: LocalDate, weeklyDaysOff: Set<DayOfWeek>): List<LocalDate> {
        val span = ChronoUnit.DAYS.between(from, to)
        return (0..span).map(from::plusDays).filter { it.dayOfWeek !in weeklyDaysOff }
    }

    private fun follows(last: LocalDate, next: LocalDate, weeklyDaysOff: Set<DayOfWeek>): Boolean {
        val gap = ChronoUnit.DAYS.between(last, next)
        return (1 until gap).none { last.plusDays(it).dayOfWeek !in weeklyDaysOff }
    }
}
