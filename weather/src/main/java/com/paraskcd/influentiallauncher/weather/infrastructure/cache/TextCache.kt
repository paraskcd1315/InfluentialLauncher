// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.infrastructure.cache

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextCache @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun read(folder: String, name: String): CachedText? = withContext(Dispatchers.IO) {
        val file = File(folderOf(folder), name)
        if (!file.exists()) null else CachedText(file.readText(), file.lastModified())
    }

    suspend fun write(folder: String, name: String, text: String) = withContext(Dispatchers.IO) {
        File(folderOf(folder), name).writeText(text)
    }

    private fun folderOf(name: String): File = File(context.filesDir, name).apply { mkdirs() }
}
