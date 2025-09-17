package com.paraskcd.influentiallauncher.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.data.db.entities.LauncherScreenEntity
import com.paraskcd.influentiallauncher.data.db.repositories.LauncherScreenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScreenManagerViewModel @Inject constructor(
    private val repo: LauncherScreenRepository
): ViewModel() {
    val screens: StateFlow<List<LauncherScreenEntity>> = repo.screens
                                                            .map { it.sortedBy { s -> s.rank } }
                                                            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun addScreen(): Long {
        val nextRank = screens.value.size
        return repo.insert(
            LauncherScreenEntity(rank = nextRank + 1, isDefault = false)
        )
    }

    fun deleteScreen(id: Long) = viewModelScope.launch {
        val wasDefault = screens.value.firstOrNull { it.id == id }?.isDefault == true
        repo.deleteById(id)
        // Asegurar default si se borró el predeterminado
        val remaining = screens.value.filter { it.id != id }.sortedBy { it.rank }
        val fallbackId = remaining.firstOrNull { it.isDefault }?.id ?: remaining.firstOrNull()?.id
        if (wasDefault && fallbackId != null) {
            repo.setDefault(fallbackId)
        }
    }

    fun setDefault(id: Long) = viewModelScope.launch {
        repo.setDefault(id)
    }

    fun moveUp(id: Long) = viewModelScope.launch {
        val list = screens.value.sortedBy { it.rank }.toMutableList()
        val idx = list.indexOfFirst { it.id == id }
        if (idx > 0) {
            list.add(idx - 1, list.removeAt(idx))
            repo.reorder(list.map { it.id })
        }
    }

    fun moveDown(id: Long) = viewModelScope.launch {
        val list = screens.value.sortedBy { it.rank }.toMutableList()
        val idx = list.indexOfFirst { it.id == id }
        if (idx >= 0 && idx < list.lastIndex) {
            list.add(idx + 1, list.removeAt(idx))
            repo.reorder(list.map { it.id })
        }
    }
}