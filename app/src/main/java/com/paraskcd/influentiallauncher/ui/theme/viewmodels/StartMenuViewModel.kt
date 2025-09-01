package com.paraskcd.influentiallauncher.ui.theme.viewmodels

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.data.db.repositories.AppShortcutRepository
import com.paraskcd.influentiallauncher.data.managers.AppRepositoryManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartMenuViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val appRepositoryManager: AppRepositoryManager,
    private val repo: AppShortcutRepository,
): ViewModel() {
    val query = MutableStateFlow("")
    private val apps = appRepositoryManager.apps

    val filteredApps = combine(query, apps) { q, list ->
        val trim = q.trim()
        if (trim.isEmpty()) list
        else list.filter { it.label.contains(trim, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun pinDock(pkg: String, activity: String?, label: String, rank: Int) = viewModelScope.launch { repo.pinToDock(pkg, activity, label, rank) }

    fun placeHome(pkg: String, activity: String?, label: String, screen: Int, row: Int, column: Int) = viewModelScope.launch { repo.placeOnHome(pkg, activity, label, screen, row, column) }

    fun openAppInfo(pkg: String) = appRepositoryManager.openAppInfo(pkg)

    fun uninstallApp(pkg: String) = appRepositoryManager.uninstallApp(pkg)
}