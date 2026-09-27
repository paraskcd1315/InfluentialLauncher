package com.paraskcd.influentiallauncher.homescreen.domain.usecase

import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.pins.domain.model.PinTarget
import com.paraskcd.influentiallauncher.pins.domain.usecase.PinnedApps
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeDock @Inject constructor(
    private val home: HomeScreen,
    private val pins: PinnedApps
) {
    suspend fun toTaskbar(app: AppId, index: Int) {
        home.remove(app)
        placeOnTaskbar(app, index)
    }

    suspend fun reorderTaskbar(app: AppId, index: Int) = placeOnTaskbar(app, index)

    suspend fun toHome(app: AppId, pageId: String?, index: Int) {
        pins.reorder(PinTarget.Taskbar, pins.pinnedIds(PinTarget.Taskbar).first() - app)
        val target = pageId ?: home.addPage()
        home.move(app, target, index)
    }

    private suspend fun placeOnTaskbar(app: AppId, index: Int) {
        val others = pins.pinnedIds(PinTarget.Taskbar).first() - app
        pins.reorder(PinTarget.Taskbar, others.toMutableList().apply { add(index.coerceIn(0, size), app) })
    }
}
