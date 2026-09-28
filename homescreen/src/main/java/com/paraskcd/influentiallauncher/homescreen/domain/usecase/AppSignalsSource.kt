package com.paraskcd.influentiallauncher.homescreen.domain.usecase

import com.paraskcd.influentiallauncher.homescreen.domain.model.AppSignals
import com.paraskcd.influentiallauncher.notifications.domain.ports.NotificationBadges
import com.paraskcd.influentiallauncher.tasks.domain.ports.OpenApps
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class AppSignalsSource @Inject constructor(
    badges: NotificationBadges,
    openApps: OpenApps
) {
    val signals: Flow<AppSignals> = combine(badges.counts, openApps.taskCounts, ::AppSignals)
}
