package com.paraskcd.influentiallauncher.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.paraskcd.influentiallauncher.data.db.dao.AppShortcutDao
import com.paraskcd.influentiallauncher.data.db.dao.LauncherScreenDao
import com.paraskcd.influentiallauncher.data.db.entities.AppShortcutEntity
import com.paraskcd.influentiallauncher.data.db.entities.LauncherScreenEntity

@Database(
    entities = [AppShortcutEntity::class, LauncherScreenEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appShortcutDao(): AppShortcutDao
    abstract fun launcherScreenDao(): LauncherScreenDao
}