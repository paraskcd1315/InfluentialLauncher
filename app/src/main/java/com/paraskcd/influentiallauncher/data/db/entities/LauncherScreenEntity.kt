package com.paraskcd.influentiallauncher.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "launcher_screens"
)
data class LauncherScreenEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val rank: Int,
    val isDefault: Boolean
)