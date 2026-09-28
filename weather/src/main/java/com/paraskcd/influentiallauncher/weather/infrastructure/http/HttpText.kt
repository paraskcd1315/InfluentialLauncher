// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.infrastructure.http

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HttpText @Inject constructor() {

    suspend fun get(url: String, headers: Map<String, String> = emptyMap(), fallbackCharset: Charset = Charsets.UTF_8): String =
        withContext(Dispatchers.IO) {
            val connection = (URL(url).openConnection() as HttpURLConnection).apply {
                connectTimeout = TimeoutMs
                readTimeout = TimeoutMs
                setRequestProperty("Accept", "application/json")
                headers.forEach { (name, value) -> setRequestProperty(name, value) }
            }
            try {
                val code = connection.responseCode
                if (code !in 200..299) throw IOException("HTTP $code for ${connection.url.host}${connection.url.path}")
                val charset = charsetOf(connection.contentType) ?: fallbackCharset
                connection.inputStream.use { it.readBytes().toString(charset) }
            } finally {
                connection.disconnect()
            }
        }

    private fun charsetOf(contentType: String?): Charset? =
        contentType?.split(';')
            ?.map { it.trim() }
            ?.firstOrNull { it.startsWith("charset=", ignoreCase = true) }
            ?.substringAfter('=')
            ?.let { runCatching { Charset.forName(it) }.getOrNull() }

    private companion object {
        const val TimeoutMs = 10_000
    }
}
