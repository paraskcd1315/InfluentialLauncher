// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.infrastructure.aemet

import android.util.Log
import com.paraskcd.influentiallauncher.weather.BuildConfig
import com.paraskcd.influentiallauncher.weather.infrastructure.cache.TextCache
import com.paraskcd.influentiallauncher.weather.infrastructure.http.ApiUrl
import com.paraskcd.influentiallauncher.weather.infrastructure.http.HttpText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONObject
import java.nio.charset.Charset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AemetFetcher @Inject constructor(
    private val http: HttpText,
    private val cache: TextCache
) {
    private val key = BuildConfig.AEMET_API_KEY
    private val latin = Charset.forName(AemetApi.FallbackCharset)
    private val lock = Mutex()
    private var pausedUntilMs = 0L

    val configured: Boolean get() = key.isNotBlank()

    suspend fun text(cacheName: String, path: String, maxAgeMs: Long): String? = lock.withLock {
        val cached = cache.read(FolderName, cacheName)
        if (cached != null && cached.ageMs() < maxAgeMs) return@withLock cached.text
        if (!configured || System.currentTimeMillis() < pausedUntilMs) return@withLock cached?.text
        runCatching { fetch(path) }
            .onSuccess { cache.write(FolderName, cacheName, it) }
            .onFailure {
                if (it is CancellationException) throw it
                pausedUntilMs = System.currentTimeMillis() + FailurePauseMs
                Log.w(LogTag, "request failed for $cacheName", it)
            }
            .getOrNull() ?: cached?.text
    }

    private suspend fun fetch(path: String): String {
        val body = http.get(ApiUrl.of(AemetApi.BaseUrl, path), mapOf(AemetApi.ApiKeyHeader to key), latin).trim()
        if (body.startsWith(JsonArrayStart)) return body
        return data(JSONObject(body).getString(AemetApi.DataField))
    }

    private suspend fun data(url: String): String {
        val first = runCatching { http.get(url, fallbackCharset = latin).trim() }.getOrNull()
        if (first != null && first.startsWith(JsonArrayStart)) return first
        delay(DataRetryDelayMs)
        val second = http.get(url, fallbackCharset = latin).trim()
        check(second.startsWith(JsonArrayStart)) { "AEMET data is not a JSON array" }
        return second
    }

    private companion object {
        const val LogTag = "AemetFetcher"
        const val FolderName = "aemet"
        const val JsonArrayStart = "["
        const val FailurePauseMs = 15 * 60 * 1000L
        const val DataRetryDelayMs = 1_500L
    }
}
