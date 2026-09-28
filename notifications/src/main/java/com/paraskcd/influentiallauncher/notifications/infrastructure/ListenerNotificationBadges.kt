package com.paraskcd.influentiallauncher.notifications.infrastructure

import com.paraskcd.influentiallauncher.notifications.domain.ports.NotificationBadges
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListenerNotificationBadges @Inject constructor() : NotificationBadges {
    private val current = MutableStateFlow<Map<String, Int>>(emptyMap())
    override val counts: StateFlow<Map<String, Int>> = current.asStateFlow()

    fun publish(counts: Map<String, Int>) {
        current.value = counts
    }
}
