package com.paraskcd.influentiallauncher.data.interfaces

import com.paraskcd.influentiallauncher.data.db.entities.AppShortcutEntity

sealed interface GridCell {
    data class App(val entity: AppShortcutEntity): GridCell
    object Empty: GridCell
}