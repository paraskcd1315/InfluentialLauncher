package com.paraskcd.influentiallauncher.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.data.db.entities.LauncherScreenEntity
import com.paraskcd.influentiallauncher.data.db.repositories.LauncherScreenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LauncherStateViewModel @Inject constructor(
    private val repo: LauncherScreenRepository,
    private val savedState: SavedStateHandle
) : ViewModel() {
    val screens: StateFlow<List<LauncherScreenEntity>> =
        repo.screens
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val _activeScreenId = MutableStateFlow<Long?>(savedState.get<Long?>("active_screen"))
    val activeScreenId: StateFlow<Long?> = _activeScreenId

    val activeScreenIndex: StateFlow<Int> =
        combine(screens, activeScreenId) { list, id ->
            if (id == null) -1 else list.indexOfFirst { it.id == id }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), -1)

    init {
        viewModelScope.launch {
            repo.ensureDefaultScreen()
        }
        viewModelScope.launch {
            repo.screens.collect { list ->
                if (_activeScreenId.value == null && list.isNotEmpty()) {
                    val def = list.firstOrNull { it.isDefault } ?: list.minByOrNull { it.rank }
                    setActiveScreen(def?.id, persistAsDefault = false)
                }
            }
        }
    }

    fun setActiveScreen(id: Long?, persistAsDefault: Boolean = false) {
        _activeScreenId.value = id
        savedState["active_screen"] = id
        if (persistAsDefault && id != null) {
            viewModelScope.launch { repo.setDefault(id) }
        }
    }

    fun setActiveScreenByIndex(index: Int, persistAsDefault: Boolean = false) {
        val list = screens.value
        if (index in list.indices) {
            setActiveScreen(list[index].id, persistAsDefault)
        }
    }
}