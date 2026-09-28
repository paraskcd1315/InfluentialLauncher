// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.infrastructure.meteocat

data class CachedText(val text: String, val savedAtMs: Long) {
    fun ageMs(nowMs: Long = System.currentTimeMillis()): Long = nowMs - savedAtMs
}
