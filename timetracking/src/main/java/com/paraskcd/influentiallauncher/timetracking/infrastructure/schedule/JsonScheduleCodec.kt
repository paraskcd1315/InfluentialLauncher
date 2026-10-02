// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.infrastructure.schedule

import com.paraskcd.influentiallauncher.timetracking.domain.model.WorkSchedule
import com.paraskcd.influentiallauncher.timetracking.domain.ports.ScheduleCodec
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JsonScheduleCodec @Inject constructor() : ScheduleCodec {

    override fun read(text: String): WorkSchedule? {
        val trimmed = text.trim().trimEnd(',')
        if (trimmed.isEmpty()) return null
        val root = parse(trimmed) ?: parse("{$trimmed}") ?: return null
        val held = root.optJSONObject(ScheduleFields.Branch) ?: root
        if (Known.none(held::has)) return null
        return WorkSchedule(
            weeklyHours = held.optDouble(ScheduleFields.WeeklyHours, WorkSchedule.DefaultWeeklyHours),
            hoursByMonth = hoursByMonth(held.optJSONObject(ScheduleFields.HoursByMonth)),
            workdays = held.optInt(ScheduleFields.Workdays, WorkSchedule.DefaultWorkdays),
            holidays = holidays(held)
        )
    }

    override fun write(schedule: WorkSchedule): String {
        val months = JSONObject()
        schedule.hoursByMonth.toSortedMap().forEach { (month, hours) -> months.put(month.toString(), hours) }
        val holidays = JSONArray()
        schedule.holidays.sorted().forEach { holidays.put(JSONObject().put(ScheduleFields.HolidayDate, it.toString())) }
        return JSONObject()
            .put(ScheduleFields.WeeklyHours, schedule.weeklyHours)
            .put(ScheduleFields.HoursByMonth, months)
            .put(ScheduleFields.Workdays, schedule.workdays)
            .put(ScheduleFields.Holidays, holidays)
            .toString()
    }

    private fun parse(text: String): JSONObject? = runCatching { JSONObject(text) }.getOrNull()

    private fun hoursByMonth(held: JSONObject?): Map<Int, Double> {
        if (held == null) return emptyMap()
        return held.keys().asSequence().mapNotNull { key ->
            val month = key.toIntOrNull() ?: return@mapNotNull null
            val hours = held.optDouble(key).takeUnless { it.isNaN() } ?: return@mapNotNull null
            month to hours
        }.toMap()
    }

    private fun holidays(held: JSONObject): Set<LocalDate> {
        val list = held.optJSONArray(ScheduleFields.Holidays) ?: return emptySet()
        return (0 until list.length()).mapNotNull { index ->
            val date = list.optJSONObject(index)?.optString(ScheduleFields.HolidayDate)?.trim()
            runCatching { LocalDate.parse(date) }.getOrNull()
        }.toSet()
    }

    private companion object {
        val Known = listOf(
            ScheduleFields.WeeklyHours,
            ScheduleFields.HoursByMonth,
            ScheduleFields.Workdays,
            ScheduleFields.Holidays
        )
    }
}
