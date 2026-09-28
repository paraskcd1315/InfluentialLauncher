package com.paraskcd.influentiallauncher.weather.infrastructure.meteocat

import android.util.Log
import com.paraskcd.influentiallauncher.weather.BuildConfig
import com.paraskcd.influentiallauncher.weather.infrastructure.http.ApiUrl
import com.paraskcd.influentiallauncher.weather.infrastructure.http.HttpText
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MeteocatFetcher @Inject constructor(
    private val http: HttpText,
    private val cache: MeteocatCache,
    private val budget: MeteocatBudget
) {
    private val key = BuildConfig.METEOCAT_API_KEY
    private val lock = Mutex()

    val configured: Boolean get() = key.isNotBlank()

    suspend fun text(cacheName: String, path: String, plan: MeteocatPlan?, maxAgeMs: Long): String? = lock.withLock {
        val cached = cache.read(cacheName)
        if (cached != null && cached.ageMs() < maxAgeMs) return@withLock cached.text
        if (!configured) return@withLock cached?.text
        if (plan != null && !budget.take(plan)) return@withLock cached?.text
        runCatching { http.get(ApiUrl.of(MeteocatApi.BaseUrl, path), mapOf(MeteocatApi.ApiKeyHeader to key)) }
            .onSuccess { cache.write(cacheName, it) }
            .onFailure { Log.w(LogTag, "request failed for $cacheName", it) }
            .getOrNull() ?: cached?.text
    }

    private companion object {
        const val LogTag = "MeteocatFetcher"
    }
}
