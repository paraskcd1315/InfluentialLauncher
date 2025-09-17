package com.paraskcd.influentiallauncher.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.data.db.entities.AppShortcutEntity
import com.paraskcd.influentiallauncher.data.db.entities.LauncherScreenEntity
import com.paraskcd.influentiallauncher.data.db.repositories.AppShortcutRepository
import com.paraskcd.influentiallauncher.data.db.repositories.LauncherScreenRepository
import com.paraskcd.influentiallauncher.data.interfaces.GridCell
import com.paraskcd.influentiallauncher.data.managers.AppRepositoryManager
import com.paraskcd.influentiallauncher.data.managers.BatteryStatusManager
import com.paraskcd.influentiallauncher.data.managers.CellularStatusManager
import com.paraskcd.influentiallauncher.data.managers.WifiStatusManager
import com.paraskcd.influentiallauncher.data.types.UiCell
import com.paraskcd.influentiallauncher.services.SystemActionsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.insert

@HiltViewModel
class LauncherItemsViewModel @Inject constructor(
    private val appRepositoryManager: AppRepositoryManager,
    private val repo: AppShortcutRepository,
    private val batteryStatusManager: BatteryStatusManager,
    private val cellularStatusManager: CellularStatusManager,
    private val wifiStatusManager: WifiStatusManager,
    private val systemActionsService: SystemActionsService,
    private val launcherScreenRepository: LauncherScreenRepository
): ViewModel() {
    private val _dockEditMode = MutableStateFlow(false)
    val dockEditMode: StateFlow<Boolean> = _dockEditMode

    private val _homeEditMode = MutableStateFlow(false)
    val homeEditMode: StateFlow<Boolean> = _homeEditMode

    val wifiLevel = wifiStatusManager.level.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val cellularLevel: StateFlow<Int> = cellularStatusManager.level.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val cellularNetworkType: StateFlow<String> = cellularStatusManager.networkType.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val batteryLevel: StateFlow<Int> = batteryStatusManager.level.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val isCharging: StateFlow<Boolean> = batteryStatusManager.isCharging.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val isFullCharge: StateFlow<Boolean> = batteryStatusManager.isFullCharge.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val screens = launcherScreenRepository.screens.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val activeScreenId = MutableStateFlow<Long?>(null)
    val dock: StateFlow<List<AppShortcutEntity>> = repo.dock.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val home: StateFlow<List<AppShortcutEntity>> = repo.home.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        viewModelScope.launch {
            launcherScreenRepository.ensureDefaultScreen()
        }
        viewModelScope.launch {
            screens.collect { list ->
                if (activeScreenId.value == null) {
                    activeScreenId.value = list.firstOrNull { it.isDefault }?.id ?: list.firstOrNull()?.id
                }
            }
        }
    }

    fun setActiveScreen(id: Long) { activeScreenId.value = id }

    fun moveDock(id: Long, newRank: Int) = viewModelScope.launch { repo.moveDock(id, newRank) }

    fun moveHome(id: Long, screenId: Long, row: Int, column: Int) = viewModelScope.launch { repo.moveHome(id, screenId, row, column) }

    fun swapHomeItems(screenId: Long, from: UiCell, fromRow: Int, fromCol: Int, to: UiCell, toRow: Int, toCol: Int) = viewModelScope.launch {
        val fromApp = (from.cell as? GridCell.App)?.entity
        val toApp = (to.cell as? GridCell.App)?.entity

        repo.swapHomeItems(screenId, fromApp, fromRow, fromCol, toApp, toRow, toCol)
    }


    fun remove(id: Long) = viewModelScope.launch { repo.remove(id) }

    fun clearDock() = viewModelScope.launch { repo.clearDock() }

    fun clearHome() = viewModelScope.launch { repo.clearHome() }

    fun getBatteryDrawable(levelBucket: Int, charging: Boolean, isFullCharge: Boolean): Int = batteryStatusManager.getBatteryDrawable(levelBucket, charging, isFullCharge)
    fun getCellularDrawable(level: Int): Int = cellularStatusManager.getCellularDrawableForLevel(level)

    fun getWifiDrawable(level: Int): Int = wifiStatusManager.getWifiDrawableForLevel(level)

    fun openNotifications(): Boolean = systemActionsService.openNotifications()

    fun getAppIcons(pkg: String, dynamicColor: Int? = null) = appRepositoryManager.getAppIcon(packageName = pkg, applyDynamicColoring = true, dynamicColor = dynamicColor)

    fun launchApp(pkg: String) = appRepositoryManager.launchApp(pkg)

    fun openAppInfo(pkg: String) = appRepositoryManager.openAppInfo(pkg)

    fun uninstallApp(pkg: String) = appRepositoryManager.uninstallApp(pkg)

    fun updateDockOrder(newOrder: List<AppShortcutEntity>) {
        viewModelScope.launch {
            repo.updateDockOrder(newOrder)
        }
    }

    fun setDockEditMode(enabled: Boolean) {
        _dockEditMode.value = enabled
    }

    fun setHomeEditMode(enabled: Boolean) {
        _homeEditMode.value = enabled
    }

    suspend fun addScreen(): Long {
        val current = screens.first()
        val nextRank = (current.maxOfOrNull { it.rank } ?: -1) + 1
        val id = launcherScreenRepository.insert(
            LauncherScreenEntity(rank = nextRank, isDefault = current.isEmpty())
        )
        activeScreenId.value = id
        return id
    }

    fun deleteScreen(id: Long) = viewModelScope.launch {
        val current = screens.first()
        if (current.size <= 1) return@launch
        val wasActive = activeScreenId.value == id
        val wasDefault = current.firstOrNull { it.id == id }?.isDefault == true

        launcherScreenRepository.deleteById(id)

        val remaining = screens.first()
        val fallbackId = remaining.firstOrNull { it.isDefault }?.id
            ?: remaining.firstOrNull()?.id
        if (wasActive) fallbackId?.let { activeScreenId.value = it }
        if (wasDefault && fallbackId != null) launcherScreenRepository.setDefault(fallbackId)
    }

    fun setDefaultScreen(id: Long) = viewModelScope.launch {
        launcherScreenRepository.setDefault(id)
    }
}