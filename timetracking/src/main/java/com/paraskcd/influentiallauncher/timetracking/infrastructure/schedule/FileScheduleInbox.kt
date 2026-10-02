// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.infrastructure.schedule

import android.content.Context
import android.util.Log
import com.paraskcd.influentiallauncher.timetracking.domain.ports.ScheduleInbox
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileScheduleInbox @Inject constructor(
    @ApplicationContext private val context: Context
) : ScheduleInbox {

    override suspend fun take(): String? = withContext(Dispatchers.IO) {
        runCatching {
            val file = File(context.getExternalFilesDir(null) ?: return@runCatching null, FileName)
            if (!file.exists()) return@runCatching null
            val text = file.readText()
            file.delete()
            Log.i(LogTag, "work schedule taken from the inbox file")
            text
        }.onFailure { Log.w(LogTag, "inbox read failed", it) }.getOrNull()
    }

    private companion object {
        const val LogTag = "ScheduleInbox"
        const val FileName = "work-schedule.json"
    }
}
