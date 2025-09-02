package com.paraskcd.influentiallauncher.data.db.repositories

import com.paraskcd.influentiallauncher.data.db.dao.LauncherScreenDao
import com.paraskcd.influentiallauncher.data.db.entities.LauncherScreenEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LauncherScreenRepository @Inject constructor(
    private val dao: LauncherScreenDao
) {
    val screens: Flow<List<LauncherScreenEntity>> = dao.observeAllScreens()

    suspend fun ensureDefaultScreen() {
        if (dao.count() == 0) {
            insert(
                LauncherScreenEntity(rank = 0, isDefault = true)
            )
        }
    }

    suspend fun insert(screen: LauncherScreenEntity): Long {
        return dao.insert(screen)
    }

    suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }

    suspend fun setDefault(id: Long) {
        dao.setDefault(id)
    }
}