// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.pins.domain.usecase

import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.apps.domain.ports.InstalledApps
import com.paraskcd.influentiallauncher.pins.domain.model.PinTarget
import com.paraskcd.influentiallauncher.pins.domain.ports.PinStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PinnedApps @Inject constructor(
    private val installedApps: InstalledApps,
    private val pinStore: PinStore
) {
    fun pinned(target: PinTarget): Flow<List<LauncherApp>> =
        combine(installedApps.apps, pinStore.pins(target)) { apps, pins ->
            val byId = apps.associateBy { it.id }
            pins.mapNotNull { byId[it] }
        }

    fun pinnedIds(target: PinTarget): Flow<List<AppId>> = pinStore.pins(target)

    suspend fun toggle(target: PinTarget, id: AppId) {
        val current = pinStore.pins(target).first()
        pinStore.save(target, if (id in current) current - id else current + id)
    }

    suspend fun reorder(target: PinTarget, order: List<AppId>) {
        pinStore.save(target, order.distinct())
    }
}
