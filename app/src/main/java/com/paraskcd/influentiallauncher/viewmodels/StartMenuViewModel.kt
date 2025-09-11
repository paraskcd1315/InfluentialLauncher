package com.paraskcd.influentiallauncher.viewmodels

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.data.db.entities.LauncherScreenEntity
import com.paraskcd.influentiallauncher.data.db.repositories.AppShortcutRepository
import com.paraskcd.influentiallauncher.data.db.repositories.LauncherScreenRepository
import com.paraskcd.influentiallauncher.data.managers.AppRepositoryManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartMenuViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val appRepositoryManager: AppRepositoryManager,
    private val repo: AppShortcutRepository,
    private val launcherScreenRepository: LauncherScreenRepository
): ViewModel() {
    val query = MutableStateFlow("")
    private val apps = appRepositoryManager.apps

    private val gridRows = 5
    private val gridCols = 4

    val filteredApps = combine(query, apps) { q, list ->
        val trim = q.trim()
        if (trim.isEmpty()) list
        else list.filter { it.label.contains(trim, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun pinDock(pkg: String, activity: String?, label: String, rank: Int) = viewModelScope.launch { repo.pinToDock(pkg, activity, label, rank) }

    fun placeHome(pkg: String, activity: String?, label: String) = viewModelScope.launch {
        launcherScreenRepository.ensureDefaultScreen()

        val screens = launcherScreenRepository.screens.first().sortedBy { it.rank }
        val homeItems = repo.home.first()

        var targetScreenId: Long? = null
        var targetRow = 0
        var targetColumn = 0

        for (screen in screens.asReversed()) {
            val occupied: Set<Pair<Int, Int>> = homeItems.asSequence()
                .filter { it.screenId == screen.id && it.row != null && it.column != null }
                .map { it.row!! to it.column!! }
                .toSet()

            var found = false
            outer@ for (r in 0 until gridRows) {
                for (c in 0 until gridCols) {
                    if ((r to c) !in occupied) {
                        targetScreenId = screen.id
                        targetRow = r
                        targetColumn = c
                        found = true
                        break@outer
                    }
                }
            }
            if (found) break
        }

        if (targetScreenId == null) {
            val nextRank = (screens.maxOfOrNull { it.rank } ?: -1) + 1
            val newId = launcherScreenRepository.insert(
                LauncherScreenEntity(rank = nextRank, isDefault = false)
            )
            targetScreenId = newId
            targetRow = 0
            targetColumn = 0
        }
        repo.placeOnHome(pkg, activity, label, targetScreenId, targetRow, targetColumn)
    }

    fun openAppInfo(pkg: String) = appRepositoryManager.openAppInfo(pkg)

    fun uninstallApp(pkg: String) = appRepositoryManager.uninstallApp(pkg)

    fun getAppIcons(pkg: String, dynamicColor: Int? = null) = appRepositoryManager.getAppIcon(packageName = pkg, applyDynamicColoring = true, dynamicColor = dynamicColor)
}