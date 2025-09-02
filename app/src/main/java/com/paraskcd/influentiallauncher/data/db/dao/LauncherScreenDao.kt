package com.paraskcd.influentiallauncher.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.paraskcd.influentiallauncher.data.db.entities.LauncherScreenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LauncherScreenDao {
    @Query("SELECT * FROM launcher_screens ORDER BY rank")
    fun observeAllScreens(): Flow<List<LauncherScreenEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(screen: LauncherScreenEntity): Long

    @Query("DELETE FROM launcher_screens WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE launcher_screens SET isDefault = (id = :id)")
    suspend fun setDefault(id: Long)

    @Query("SELECT COUNT(*) FROM launcher_screens")
    suspend fun count(): Int
}