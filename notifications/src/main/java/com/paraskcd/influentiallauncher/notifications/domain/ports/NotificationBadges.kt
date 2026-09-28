package com.paraskcd.influentiallauncher.notifications.domain.ports

import kotlinx.coroutines.flow.StateFlow

interface NotificationBadges {
    val counts: StateFlow<Map<String, Int>>
}
