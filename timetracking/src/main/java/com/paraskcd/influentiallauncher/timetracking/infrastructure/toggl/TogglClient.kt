// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.infrastructure.toggl

import android.util.Base64
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TogglClient @Inject constructor(
    private val http: JsonHttp
) : TrackerClient {

    override val tracker: Tracker = Tracker.Toggl

    private val lock = Mutex()
    private var workspace: Pair<String, Long>? = null
    private var projectCache: Pair<String, List<TrackerProject>>? = null

    override suspend fun entries(credentials: TrackerCredentials, from: Instant, to: Instant): List<TimeEntry> {
        val body = get(credentials, TogglApi.Paths.TimeEntries, mapOf(TogglApi.Query.StartDate to format(from), TogglApi.Query.EndDate to format(to)))
        val projects = projects(credentials).associateBy { it.id }
        val array = JSONArray(body)
        return (0 until array.length()).mapNotNull { index -> array.optJSONObject(index)?.toEntry(projects) }
    }

    override suspend fun running(credentials: TrackerCredentials): TimeEntry? {
        val body = get(credentials, TogglApi.Paths.CurrentEntry)
        if (body.isBlank() || body == "null") return null
        return JSONObject(body).toEntry(projects(credentials).associateBy { it.id })
    }

    override suspend fun projects(credentials: TrackerCredentials): List<TrackerProject> {
        projectCache?.let { (token, list) -> if (token == credentials.togglToken) return list }
        val array = JSONArray(get(credentials, TogglApi.Paths.Projects))
        val list = (0 until array.length()).mapNotNull { index ->
            val item = array.optJSONObject(index) ?: return@mapNotNull null
            if (!item.optBoolean("active", true)) return@mapNotNull null
            TrackerProject(
                id = item.getLong("id").toString(),
                name = item.optString("name"),
                clientName = item.optStringOrNull("client_name"),
                colourArgb = Colours.parse(item.optStringOrNull("color"))
            )
        }.sortedBy { it.name.lowercase() }
        projectCache = credentials.togglToken to list
        return list
    }

    override suspend fun activities(credentials: TrackerCredentials, projectId: String?): List<TrackerActivity> = emptyList()

    override suspend fun start(credentials: TrackerCredentials, timer: StartTimer) {
        val workspaceId = workspaceId(credentials)
        val body = JSONObject()
            .put("created_with", CreatedWith)
            .put("description", timer.description)
            .put("workspace_id", workspaceId)
            .put("start", format(Instant.now()))
            .put("duration", -1)
        timer.projectId?.toLongOrNull()?.let { body.put("project_id", it) }
        send(credentials, "POST", TogglApi.Paths.WorkspaceEntries.format(workspaceId), body)
    }

    override suspend fun stop(credentials: TrackerCredentials, entry: TimeEntry) {
        send(credentials, "PATCH", TogglApi.Paths.StopEntry.format(workspaceId(credentials), entry.id), null)
    }

    override suspend fun move(credentials: TrackerCredentials, entry: TimeEntry, start: Instant, end: Instant?) {
        val body = JSONObject().put("start", format(start))
        if (end != null) {
            body.put("stop", format(end))
            body.put("duration", end.epochSecond - start.epochSecond)
        } else {
            body.put("duration", -1)
        }
        send(credentials, "PUT", TogglApi.Paths.WorkspaceEntry.format(workspaceId(credentials), entry.id), body)
    }

    private suspend fun workspaceId(credentials: TrackerCredentials): Long = lock.withLock {
        workspace?.let { (token, id) -> if (token == credentials.togglToken) return id }
        val id = JSONObject(get(credentials, TogglApi.Paths.Me)).getLong("default_workspace_id")
        workspace = credentials.togglToken to id
        id
    }

    private suspend fun get(credentials: TrackerCredentials, path: String, query: Map<String, Any?> = emptyMap()): String =
        http.request("GET", ApiUrl.of(TogglApi.BaseUrl, path, query), auth(credentials))

    private suspend fun send(credentials: TrackerCredentials, method: String, path: String, body: JSONObject?) {
        http.request(method, ApiUrl.of(TogglApi.BaseUrl, path), auth(credentials), body?.toString())
    }

    private fun auth(credentials: TrackerCredentials): String {
        val raw = "${credentials.togglToken.trim()}:api_token".toByteArray()
        return "Basic " + Base64.encodeToString(raw, Base64.NO_WRAP)
    }

    private fun JSONObject.toEntry(projects: Map<String, TrackerProject>): TimeEntry? {
        val start = optStringOrNull("start")?.let(::parse) ?: return null
        val projectId = if (isNull("project_id")) null else optLong("project_id").toString()
        val project = projectId?.let(projects::get)
        return TimeEntry(
            id = getLong("id").toString(),
            tracker = Tracker.Toggl,
            description = optString("description"),
            projectId = projectId,
            projectName = project?.name,
            colourArgb = project?.colourArgb,
            start = start,
            end = optStringOrNull("stop")?.let(::parse)
        )
    }

    private fun JSONObject.optStringOrNull(name: String): String? =
        if (isNull(name)) null else optString(name).takeIf { it.isNotBlank() }

    private fun format(instant: Instant): String = DateTimeFormatter.ISO_INSTANT.format(instant.truncatedTo(ChronoUnit.SECONDS))

    private fun parse(value: String): Instant? = runCatching { OffsetDateTime.parse(value).toInstant() }.getOrNull()

    private companion object {
        const val CreatedWith = "InfluentialLauncher"
    }
}
