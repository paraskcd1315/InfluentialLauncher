package com.paraskcd.influentiallauncher.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.paraskcd.influentiallauncher.data.enums.ShortcutArea

@Entity(
    tableName = "app_shortcuts",
    indices = [
        Index(value = ["packageName", "activityName", "area"], unique = true)
    ]
)
data class AppShortcutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val packageName: String,
    val activityName: String?,
    val label: String,
    val area: ShortcutArea,

    // For HOME
    val screen: Int? = null,
    val row: Int? = null,
    val column: Int? = null,

    // For DOCK
    val rank: Int? = null
)
