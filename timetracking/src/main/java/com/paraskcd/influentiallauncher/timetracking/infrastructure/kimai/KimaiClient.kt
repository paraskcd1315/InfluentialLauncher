// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.infrastructure.kimai

import com.paraskcd.influentiallauncher.timetracking.domain.model.StartTimer
import com.paraskcd.influentiallauncher.timetracking.domain.model.TimeEntry
import com.paraskcd.influentiallauncher.timetracking.domain.model.Tracker
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerActivity
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerCredentials
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerProject
import com.paraskcd.influentiallauncher.timetracking.domain.ports.TrackerClient
import com.paraskcd.influentiallauncher.timetracking.infrastructure.http.ApiUrl
import com.paraskcd.influentiallauncher.timetracking.infrastructure.http.JsonHttp
import com.paraskcd.influentiallauncher.timetracking.infrastructure.utils.Colours
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KimaiClient @Inject constructor(
    private val http: JsonHttp
) : TrackerClient {

    override val tracker: Tracker = Tracker.Kimai

    override suspend fun entries(credentials: TrackerCredentials, from: Instant, to: Instant): List<TimeEntry> {
        val query = mapOf(
            KimaiApi.Query.Begin to local(from),
            KimaiApi.Query.End to local(to),
            KimaiApi.Query.Full to true,
            KimaiApi.Query.Size to PageSize,
            KimaiApi.Query.Order to Ascending
        )
        return JSONArray(get(credentials, KimaiApi.Paths.Timesheets, query)).entries()
    }

    override suspend fun running(credentials: TrackerCredentials): TimeEntry? =
        JSONArray(get(credentials, KimaiApi.Paths.ActiveTimesheets, mapOf(KimaiApi.Query.Full to true))).entries().firstOrNull()

    override suspend fun projects(credentials: TrackerCredentials): List<TrackerProject> {
        val array = JSONArray(get(credentials, KimaiApi.Paths.Projects, mapOf(KimaiApi.Query.Visible to VisibleOnly)))
        return (0 until array.length()).mapNotNull { index ->
            val item = array.optJSONObject(index) ?: return@mapNotNull null
            TrackerProject(
                id = item.getInt("id").toString(),
                name = item.optString("name"),
                clientName = item.optStringOrNull("parentTitle"),
                colourArgb = Colours.parse(item.optStringOrNull("color"))
            )
        }.sortedBy { it.name.lowercase() }
    }

    override suspend fun activities(credentials: TrackerCredentials, projectId: String?): List<TrackerActivity> {
        val query = mapOf(KimaiApi.Query.Visible to VisibleOnly, KimaiApi.Query.Project to projectId)
        val array = JSONArray(get(credentials, KimaiApi.Paths.Activities, query))
        return (0 until array.length()).mapNotNull { index ->
            val item = array.optJSONObject(index) ?: return@mapNotNull null
            TrackerActivity(id = item.getInt("id").toString(), name = item.optString("name"))
        }.sortedBy { it.name.lowercase() }
    }

    override suspend fun start(credentials: TrackerCredentials, timer: StartTimer): TimeEntry? {
        val project = timer.projectId?.toIntOrNull() ?: throw IllegalArgumentException("Kimai needs a project")
        val activity = timer.activityId?.toIntOrNull() ?: throw IllegalArgumentException("Kimai needs an activity")
        val body = JSONObject()
            .put("begin", local(Instant.now()))
            .put("project", project)
            .put("activity", activity)
            .put("description", timer.description)
        send(credentials, "POST", KimaiApi.Paths.Timesheets, body)
        return null
    }

    override suspend fun stop(credentials: TrackerCredentials, entry: TimeEntry): TimeEntry? {
        send(credentials, "PATCH", KimaiApi.Paths.StopTimesheet.format(entry.id), null)
        return null
    }

    override suspend fun move(credentials: TrackerCredentials, entry: TimeEntry, start: Instant, end: Instant?): TimeEntry? {
        val body = JSONObject().put("begin", local(start))
        if (end != null) body.put("end", local(end))
        send(credentials, "PATCH", KimaiApi.Paths.Timesheet.format(entry.id), body)
        return null
    }

    private fun JSONArray.entries(): List<TimeEntry> = (0 until length()).mapNotNull { index ->
        val item = optJSONObject(index) ?: return@mapNotNull null
        val start = item.optStringOrNull("begin")?.let(::parse) ?: return@mapNotNull null
        val project = item.optJSONObject("project")
        val activity = item.optJSONObject("activity")
        val description = item.optStringOrNull("description") ?: activity?.optStringOrNull("name").orEmpty()
        TimeEntry(
            id = item.getInt("id").toString(),
            tracker = Tracker.Kimai,
            description = description,
            projectId = project?.optInt("id")?.toString(),
            projectName = project?.optStringOrNull("name"),
            colourArgb = Colours.parse(project?.optStringOrNull("color") ?: activity?.optStringOrNull("color")),
            start = start,
            end = item.optStringOrNull("end")?.let(::parse),
            activityName = activity?.optStringOrNull("name"),
            clientName = project?.optJSONObject("customer")?.optStringOrNull("name"),
            tags = item.optJSONArray("tags").strings()
        )
    }

    private suspend fun get(credentials: TrackerCredentials, path: String, query: Map<String, Any?> = emptyMap()): String =
        http.request("GET", ApiUrl.of(baseUrl(credentials), path, query), auth(credentials))

    private suspend fun send(credentials: TrackerCredentials, method: String, path: String, body: JSONObject?) {
        http.request(method, ApiUrl.of(baseUrl(credentials), path), auth(credentials), body?.toString())
    }

    private fun baseUrl(credentials: TrackerCredentials): String = credentials.kimaiUrl.trim().trimEnd('/') + KimaiApi.ApiRoot

    private fun auth(credentials: TrackerCredentials): String = "Bearer ${credentials.kimaiToken.trim()}"

    private fun JSONArray?.strings(): List<String> =
        if (this == null) emptyList() else (0 until length()).mapNotNull { index ->
            optJSONObject(index)?.optStringOrNull("name") ?: optString(index).takeIf { it.isNotBlank() }
        }

    private fun JSONObject.optStringOrNull(name: String): String? =
        if (isNull(name)) null else optString(name).takeIf { it.isNotBlank() }

    private fun local(instant: Instant): String = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(LocalFormat)

    private fun parse(value: String): Instant? = runCatching { OffsetDateTime.parse(value, OffsetFormat).toInstant() }.getOrNull()

    private companion object {
        const val PageSize = 250
        const val VisibleOnly = 1
        const val Ascending = "ASC"
        val LocalFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val OffsetFormat: DateTimeFormatter = DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .optionalStart().appendOffset("+HH:MM", "Z").optionalEnd()
            .optionalStart().appendOffset("+HHMM", "Z").optionalEnd()
            .toFormatter()
    }
}
