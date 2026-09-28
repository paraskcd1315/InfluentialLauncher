// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.notifications.domain.ports

import kotlinx.coroutines.flow.StateFlow

interface NotificationBadges {
    val counts: StateFlow<Map<String, Int>>
}
