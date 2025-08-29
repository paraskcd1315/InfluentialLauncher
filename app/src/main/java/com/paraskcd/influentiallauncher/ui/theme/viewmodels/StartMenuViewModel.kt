package com.paraskcd.influentiallauncher.ui.theme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.data.managers.AppRepositoryManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StartMenuViewModel @Inject constructor(
    repo: AppRepositoryManager
): ViewModel() {
    val query = MutableStateFlow("")
    private val apps = repo.apps

    val filteredApps = combine(query, apps) { q, list ->
        val trim = q.trim()
        if (trim.isEmpty()) list
        else list.filter { it.label.contains(trim, ignoreCase = true) }
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}