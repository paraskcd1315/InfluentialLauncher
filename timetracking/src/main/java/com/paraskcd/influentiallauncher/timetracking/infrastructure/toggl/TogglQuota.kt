// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.infrastructure.toggl

import android.util.Log
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerRefusal
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TogglQuota @Inject constructor() {
    private val calls = TogglBucket.entries.associateWith { ArrayDeque<Instant>() }
    private val remaining = mutableMapOf<TogglBucket, Int>()
    private val resetsAt = mutableMapOf<TogglBucket, Instant>()

    @Synchronized
    fun admit(bucket: TogglBucket) {
        val now = Instant.now()
        val own = recent(bucket, now)
        if (own.size >= bucket.hourlyBudget) {
            throw TrackerRefusal(secondsUntil(own.first().plus(Window), now), "launcher budget of ${bucket.hourlyBudget} an hour is used")
        }
        val resets = resetsAt[bucket]?.takeIf { now.isBefore(it) } ?: return
        val left = remaining[bucket] ?: return
        if (left <= bucket.reserve) {
            throw TrackerRefusal(secondsUntil(resets, now), "$left requests left; ${bucket.reserve} stay for the other apps")
        }
    }

    @Synchronized
    fun record(bucket: TogglBucket, headers: Map<String, String>) {
        val now = Instant.now()
        val own = recent(bucket, now)
        own.addLast(now)
        headers[RemainingHeader]?.toIntOrNull()?.let { remaining[bucket] = it }
        headers[ResetsInHeader]?.toLongOrNull()?.let { resetsAt[bucket] = now.plusSeconds(it) }
        Log.i(LogTag, "$bucket: ${own.size}/${bucket.hourlyBudget} of the launcher's hour, ${remaining[bucket]} left at Toggl")
    }

    @Synchronized
    fun refused(bucket: TogglBucket, retryAfterSeconds: Long?) {
        remaining[bucket] = 0
        resetsAt[bucket] = Instant.now().plusSeconds(retryAfterSeconds ?: DefaultRefusalSeconds)
        Log.w(LogTag, "$bucket: Toggl refused; reads wait ${retryAfterSeconds ?: DefaultRefusalSeconds} s")
    }

    private fun recent(bucket: TogglBucket, now: Instant): ArrayDeque<Instant> {
        val own = calls.getValue(bucket)
        while (own.isNotEmpty() && Duration.between(own.first(), now) >= Window) own.removeFirst()
        return own
    }

    private fun secondsUntil(moment: Instant, now: Instant): Long = Duration.between(now, moment).seconds.coerceAtLeast(1)

    private companion object {
        const val LogTag = "TogglQuota"
        const val RemainingHeader = "x-toggl-quota-remaining"
        const val ResetsInHeader = "x-toggl-quota-resets-in"
        const val DefaultRefusalSeconds = 300L
        val Window: Duration = Duration.ofHours(1)
    }
}
