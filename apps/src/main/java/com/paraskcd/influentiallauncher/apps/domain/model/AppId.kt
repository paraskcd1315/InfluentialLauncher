// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.apps.domain.model

data class AppId(val packageName: String, val activityName: String) {
    val key: String get() = "$packageName$KeySeparator$activityName"

    companion object {
        private const val KeySeparator = "/"

        fun fromKey(key: String): AppId? {
            val parts = key.split(KeySeparator, limit = 2)
            if (parts.size != 2 || parts.any { it.isBlank() }) return null
            return AppId(parts[0], parts[1])
        }
    }
}
