package com.paraskcd.influentiallauncher.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.paraskcd.influentiallauncher.data.enums.ShortcutArea
import com.paraskcd.influentiallauncher.data.enums.ShortcutType

@Entity(
    tableName = "app_shortcuts",
    indices = [
        Index(value = ["packageName", "activityName", "area"], unique = true),
        Index(value = ["screenId"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = LauncherScreenEntity::class,
            parentColumns = ["id"],
            childColumns = ["screenId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class AppShortcutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val packageName: String,
    val activityName: String?,
    val label: String,
    val area: ShortcutArea,

    // For HOME
    val screenId: Long? = null,
    val row: Int? = null,
    val column: Int? = null,

    // For DOCK
    val rank: Int? = null,

    // For WIDGETS
    val type: ShortcutType = ShortcutType.APP,
    val spanColumns: Int? = 1,
    val spanRows: Int? = 1,
    val appWidgetId: Int? = null
)
