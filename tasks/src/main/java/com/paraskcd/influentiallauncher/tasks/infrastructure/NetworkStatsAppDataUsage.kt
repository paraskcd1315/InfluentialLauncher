// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.tasks.infrastructure

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.paraskcd.influentiallauncher.tasks.domain.model.DayData
import com.paraskcd.influentiallauncher.tasks.domain.ports.AppDataUsage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkStatsAppDataUsage @Inject constructor(
    @ApplicationContext private val context: Context
) : AppDataUsage {
    private val stats = context.getSystemService(NetworkStatsManager::class.java)

    override suspend fun weekly(packageName: String): List<DayData> = withContext(Dispatchers.IO) {
        val uid = runCatching { context.packageManager.getApplicationInfo(packageName, 0).uid }.getOrNull()
            ?: return@withContext emptyList()
        val manager = stats ?: return@withContext emptyList()
        val dayMillis = TimeUnit.DAYS.toMillis(1)
        val todayStart = startOfToday()
        (Days - 1 downTo 0).map { back ->
            val start = todayStart - back * dayMillis
            val end = start + dayMillis
            DayData(
                dayStart = start,
                mobileBytes = bytes(manager, ConnectivityManager.TYPE_MOBILE, start, end, uid),
                wifiBytes = bytes(manager, ConnectivityManager.TYPE_WIFI, start, end, uid)
            )
        }
    }

    @Suppress("DEPRECATION")
    private fun bytes(manager: NetworkStatsManager, networkType: Int, start: Long, end: Long, uid: Int): Long =
        runCatching {
            val result = manager.queryDetailsForUid(networkType, null, start, end, uid)
            var total = 0L
            val bucket = NetworkStats.Bucket()
            while (result.hasNextBucket()) {
                result.getNextBucket(bucket)
                total += bucket.rxBytes + bucket.txBytes
            }
            result.close()
            total
        }.onFailure { Log.w(LogTag, "usage query failed", it) }.getOrDefault(0L)

    private fun startOfToday(): Long {
        val now = System.currentTimeMillis()
        val offset = TimeZone.getDefault().getOffset(now)
        val dayMillis = TimeUnit.DAYS.toMillis(1)
        return ((now + offset) / dayMillis) * dayMillis - offset
    }

    private companion object {
        const val LogTag = "AppDataUsage"
        const val Days = 7
    }
}
