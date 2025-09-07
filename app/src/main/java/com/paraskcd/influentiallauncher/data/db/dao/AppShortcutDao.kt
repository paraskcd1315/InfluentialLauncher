package com.paraskcd.influentiallauncher.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.paraskcd.influentiallauncher.data.db.entities.AppShortcutEntity
import com.paraskcd.influentiallauncher.data.enums.ShortcutArea
import kotlinx.coroutines.flow.Flow

@Dao
interface AppShortcutDao {
    @Query("SELECT * FROM app_shortcuts WHERE area = 'DOCK' ORDER BY COALESCE(rank, 0), id")
    fun observeDock(): Flow<List<AppShortcutEntity>>

    @Query("""
        SELECT a.* FROM app_shortcuts a
        LEFT JOIN launcher_screens s ON a.screenId = s.id
        WHERE a.area = 'HOME'
        ORDER BY COALESCE(s.rank, 0), COALESCE(a.`row`,0), COALESCE(a.`column`,0), a.id
    """)
    fun observeHome(): Flow<List<AppShortcutEntity>>

    @Upsert
    suspend fun upsert(item: AppShortcutEntity): Long

    @Upsert
    suspend fun upsertAll(items: List<AppShortcutEntity>)

    @Query("DELETE FROM app_shortcuts WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM app_shortcuts WHERE area = :area")
    suspend fun clearArea(area: ShortcutArea)

    @Query("UPDATE app_shortcuts SET rank = :newRank WHERE id = :id")
    suspend fun updateRank(id: Long, newRank: Int)

    @Query("UPDATE app_shortcuts SET screenId = :screenId, `row` = :row, `column` = :column WHERE id = :id")
    suspend fun updateGridPosition(id: Long, screenId: Long, row: Int, column: Int)

    @Transaction
    open suspend fun swapHomeItems(
        screenId: Long,
        fromApp: AppShortcutEntity?,
        fromRow: Int,
        fromCol: Int,
        toApp: AppShortcutEntity?,
        toRow: Int,
        toCol: Int
    ) {
        if (fromApp != null) {
            updateGridPosition(fromApp.id, screenId, toRow, toCol)
        }
        if (toApp != null) {
            updateGridPosition(toApp.id, screenId, fromRow, fromCol)
        }
    }
}