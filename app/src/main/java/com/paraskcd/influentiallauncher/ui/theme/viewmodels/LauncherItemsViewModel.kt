package com.paraskcd.influentiallauncher.ui.theme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.data.db.entities.AppShortcutEntity
import com.paraskcd.influentiallauncher.data.db.repositories.AppShortcutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LauncherItemsViewModel @Inject constructor(
    private val repo: AppShortcutRepository
): ViewModel() {
    val dock: StateFlow<List<AppShortcutEntity>> = repo.dock.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val home: StateFlow<List<AppShortcutEntity>> = repo.home.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun pinDock(pkg: String, activity: String?, label: String, rank: Int) = viewModelScope.launch { repo.pinToDock(pkg, activity, label, rank) }

    fun placeHome(pkg: String, activity: String?, label: String, screen: Int, row: Int, column: Int) = viewModelScope.launch { repo.placeOnHome(pkg, activity, label, screen, row, column) }

    fun moveDock(id: Long, newRank: Int) = viewModelScope.launch { repo.moveDock(id, newRank) }

    fun moveHome(id: Long, screen: Int, row: Int, column: Int) = viewModelScope.launch { repo.moveHome(id, screen, row, column) }

    fun remove(id: Long) = viewModelScope.launch { repo.remove(id) }

    fun clearDock() = viewModelScope.launch { repo.clearDock() }

    fun clearHome() = viewModelScope.launch { repo.clearHome() }
}