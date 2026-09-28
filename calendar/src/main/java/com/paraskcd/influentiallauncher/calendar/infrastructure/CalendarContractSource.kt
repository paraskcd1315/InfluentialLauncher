// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.calendar.infrastructure

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.CalendarContract
import android.util.Log
import androidx.core.content.ContextCompat
import com.paraskcd.influentiallauncher.calendar.domain.model.CalendarEvent
import com.paraskcd.influentiallauncher.calendar.domain.ports.CalendarSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarContractSource @Inject constructor(
    @ApplicationContext private val context: Context
) : CalendarSource {

    override val permission: String = Manifest.permission.READ_CALENDAR

    override fun hasPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    override fun events(day: LocalDate): Flow<List<CalendarEvent>> = callbackFlow {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                trySend(read(day))
            }
        }
        trySend(read(day))
        if (hasPermission()) {
            context.contentResolver.registerContentObserver(CalendarContract.Events.CONTENT_URI, true, observer)
        }
        awaitClose { context.contentResolver.unregisterContentObserver(observer) }
    }.flowOn(Dispatchers.IO)

    override fun open(event: CalendarEvent): Boolean = runCatching {
        val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.id)
        val intent = Intent(Intent.ACTION_VIEW, uri)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.begin.toEpochMilli())
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.end.toEpochMilli())
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }.onFailure { Log.w(LogTag, "open event failed", it) }.isSuccess

    private fun read(day: LocalDate): List<CalendarEvent> {
        if (!hasPermission()) return emptyList()
        val zone = ZoneId.systemDefault()
        val start = day.atStartOfDay(zone).toInstant().toEpochMilli()
        val end = day.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        val uri = CalendarContract.Instances.CONTENT_URI.buildUpon().also {
            ContentUris.appendId(it, start)
            ContentUris.appendId(it, end)
        }.build()
        return runCatching {
            context.contentResolver.query(uri, Projection, null, null, SortOrder)?.use { cursor ->
                buildList {
                    while (cursor.moveToNext()) {
                        add(
                            CalendarEvent(
                                id = cursor.getLong(0),
                                title = cursor.getString(1).orEmpty(),
                                begin = Instant.ofEpochMilli(cursor.getLong(2)),
                                end = Instant.ofEpochMilli(cursor.getLong(3)),
                                allDay = cursor.getInt(4) == 1,
                                colorArgb = if (cursor.isNull(5)) null else cursor.getInt(5),
                                location = cursor.getString(6)?.takeIf { it.isNotBlank() },
                                description = cursor.getString(7)?.takeIf { it.isNotBlank() },
                                calendarName = cursor.getString(8)?.takeIf { it.isNotBlank() }
                            )
                        )
                    }
                }
            }.orEmpty()
        }.onFailure { Log.w(LogTag, "reading events failed", it) }.getOrDefault(emptyList())
    }

    private companion object {
        const val LogTag = "CalendarSource"
        const val SortOrder = "${CalendarContract.Instances.ALL_DAY} DESC, ${CalendarContract.Instances.BEGIN} ASC"
        val Projection = arrayOf(
            CalendarContract.Instances.EVENT_ID,
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
            CalendarContract.Instances.ALL_DAY,
            CalendarContract.Instances.DISPLAY_COLOR,
            CalendarContract.Instances.EVENT_LOCATION,
            CalendarContract.Instances.DESCRIPTION,
            CalendarContract.Instances.CALENDAR_DISPLAY_NAME
        )
    }
}
