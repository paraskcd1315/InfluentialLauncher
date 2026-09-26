package com.paraskcd.influentiallauncher.taskbar.domain.usecase

import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.apps.domain.ports.InstalledApps
import com.paraskcd.influentiallauncher.taskbar.domain.ports.PinStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PinnedApps @Inject constructor(
    private val installedApps: InstalledApps,
    private val pinStore: PinStore
) {
    val pinned: Flow<List<LauncherApp>> = combine(installedApps.apps, pinStore.pins) { apps, pins ->
        val byId = apps.associateBy { it.id }
        pins.mapNotNull { byId[it] }
    }

    val pinnedIds: Flow<List<AppId>> = pinStore.pins

    suspend fun toggle(id: AppId) {
        val current = pinStore.pins.first()
        pinStore.save(if (id in current) current - id else current + id)
    }

    suspend fun reorder(order: List<AppId>) {
        pinStore.save(order.distinct())
    }
}
