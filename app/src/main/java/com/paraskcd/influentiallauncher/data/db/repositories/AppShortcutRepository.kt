package com.paraskcd.influentiallauncher.data.db.repositories

import com.paraskcd.influentiallauncher.data.db.dao.AppShortcutDao
import com.paraskcd.influentiallauncher.data.db.entities.AppShortcutEntity
import com.paraskcd.influentiallauncher.data.enums.ShortcutArea
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppShortcutRepository @Inject constructor(
    private val dao: AppShortcutDao
) {
    val dock: Flow<List<AppShortcutEntity>> = dao.observeDock()
    val home: Flow<List<AppShortcutEntity>> = dao.observeHome()

    suspend fun pinToDock(
        packageName: String,
        activityName: String?,
        label: String,
        rank: Int
    ): Long {
        return dao.upsert(
            AppShortcutEntity(
                packageName = packageName,
                activityName = activityName,
                label = label,
                area = ShortcutArea.DOCK,
                rank = rank
            )
        )
    }

    suspend fun placeOnHome(
        packageName: String,
        activityName: String?,
        label: String,
        screen: Int,
        row: Int,
        column: Int
    ): Long {
        return dao.upsert(
            AppShortcutEntity(
                packageName = packageName,
                activityName = activityName,
                label = label,
                area = ShortcutArea.HOME,
                screen = screen,
                row = row,
                column = column
            )
        )
    }

    suspend fun moveDock(id: Long, newRank: Int) {
        dao.updateRank(id, newRank)
    }

    suspend fun moveHome(id: Long, screen: Int, row: Int, column: Int) {
        dao.updateGridPosition(id, screen, row, column)
    }

    suspend fun remove(id: Long) {
        dao.deleteById(id)
    }

    suspend fun clearDock() {
        dao.clearArea(ShortcutArea.DOCK)
    }

    suspend fun clearHome() {
        dao.clearArea(ShortcutArea.HOME)
    }
}