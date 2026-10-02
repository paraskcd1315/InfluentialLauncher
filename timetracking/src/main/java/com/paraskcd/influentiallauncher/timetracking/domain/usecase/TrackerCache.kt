// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.domain.usecase

import com.paraskcd.influentiallauncher.timetracking.domain.model.StreamSignal
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerRefusal
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Duration
import java.time.Instant

internal class TrackerCache(private val hasStream: Boolean) {
    private val lock = Mutex()
    private val answers = mutableMapOf<String, StampedAnswer>()
    private var openedAt: Instant? = null
    private var wroteAt: Instant? = null
    private var pausedUntil: Instant? = null

    suspend fun signal(signal: StreamSignal): Boolean = lock.withLock {
        val now = Instant.now()
        when (signal) {
            StreamSignal.Open -> {
                openedAt = now
                true
            }
            StreamSignal.Closed -> {
                openedAt = null
                false
            }
            StreamSignal.Changed -> {
                val echo = wroteAt?.let { Duration.between(it, now) < EchoWindow } ?: false
                if (!echo) answers.clear()
                !echo
            }
        }
    }

    suspend fun wrote() = lock.withLock {
        wroteAt = Instant.now()
        answers.clear()
    }

    suspend fun clear() = lock.withLock {
        answers.clear()
        pausedUntil = null
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun <T> answer(key: String, read: suspend () -> T): T = lock.withLock {
        val held = answers[key]
        val now = Instant.now()
        if (held != null && isFresh(held.readAt, now)) return@withLock held.value as T
        val paused = pausedUntil?.let { now.isBefore(it) } ?: false
        if (paused) {
            if (held != null) return@withLock held.value as T
            throw TrackerRefusal(null, PausedDetail)
        }
        try {
            val value = read()
            if (answers.size >= MaxAnswers) answers.clear()
            answers[key] = StampedAnswer(value, Instant.now())
            value
        } catch (refusal: TrackerRefusal) {
            pausedUntil = Instant.now().plusSeconds(refusal.retryAfterSeconds ?: DefaultPauseSeconds)
            if (held == null) throw refusal
            held.value as T
        }
    }

    private fun isFresh(readAt: Instant, now: Instant): Boolean {
        if (!hasStream) return false
        val age = Duration.between(readAt, now)
        val opened = openedAt ?: return age < ClosedMaxAge
        if (age < ResyncMinAge) return true
        return !readAt.isBefore(opened.minus(ConnectSlack)) && age < OpenMaxAge
    }

    private companion object {
        const val MaxAnswers = 16
        const val DefaultPauseSeconds = 300L
        const val PausedDetail = "tracker reads are paused after a refusal"
        val EchoWindow: Duration = Duration.ofSeconds(5)
        val ConnectSlack: Duration = Duration.ofSeconds(10)
        val ResyncMinAge: Duration = Duration.ofMinutes(3)
        val ClosedMaxAge: Duration = Duration.ofMinutes(5)
        val OpenMaxAge: Duration = Duration.ofMinutes(15)
    }
}
