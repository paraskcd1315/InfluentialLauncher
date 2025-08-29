package com.paraskcd.influentiallauncher.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.paraskcd.influentiallauncher.data.db.entities.AppShortcutEntity
import com.paraskcd.influentiallauncher.data.enums.ShortcutArea
import kotlinx.coroutines.flow.Flow

@Dao
interface AppShortcutDao {
    @Query("SELECT * FROM app_shortcuts WHERE area = 'DOCK' ORDER BY COALESCE(rank, 0), id")
    fun observeDock(): Flow<List<AppShortcutEntity>>

    @Query("SELECT * FROM app_shortcuts WHERE area = 'DOCK' ORDER BY COALESCE(screen, 0), COALESCE(`row`, 0), COALESCE(`column`, 0), id")
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

    @Query("UPDATE app_shortcuts SET screen = :screen, `row` = :row, `column` = :column WHERE id = :id")
    suspend fun updateGridPosition(id: Long, screen: Int, row: Int, column: Int)
}