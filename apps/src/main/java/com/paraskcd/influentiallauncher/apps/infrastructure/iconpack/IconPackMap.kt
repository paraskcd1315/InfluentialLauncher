// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.apps.infrastructure.iconpack

data class IconPackMap(
    val items: Map<String, String> = emptyMap(),
    val calendars: Map<String, String> = emptyMap(),
    val backs: List<String> = emptyList(),
    val mask: String? = null,
    val upon: String? = null,
    val scale: Float = 1f
)
