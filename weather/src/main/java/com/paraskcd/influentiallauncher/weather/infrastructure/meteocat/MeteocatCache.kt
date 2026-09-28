// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.infrastructure.meteocat

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MeteocatCache @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val folder: File get() = File(context.filesDir, FolderName).apply { mkdirs() }

    suspend fun read(name: String): CachedText? = withContext(Dispatchers.IO) {
        val file = File(folder, name)
        if (!file.exists()) null else CachedText(file.readText(), file.lastModified())
    }

    suspend fun write(name: String, text: String) = withContext(Dispatchers.IO) {
        File(folder, name).writeText(text)
    }

    private companion object {
        const val FolderName = "meteocat"
    }
}
