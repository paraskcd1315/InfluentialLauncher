package com.paraskcd.influentiallauncher.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.data.db.entities.AppShortcutEntity
import com.paraskcd.influentiallauncher.data.db.repositories.AppShortcutRepository
import com.paraskcd.influentiallauncher.data.managers.AppRepositoryManager
import com.paraskcd.influentiallauncher.data.managers.BatteryStatusManager
import com.paraskcd.influentiallauncher.data.managers.CellularStatusManager
import com.paraskcd.influentiallauncher.data.managers.WifiStatusManager
import com.paraskcd.influentiallauncher.services.SystemActionsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LauncherItemsViewModel @Inject constructor(
    private val appRepositoryManager: AppRepositoryManager,
    private val repo: AppShortcutRepository,
    private val batteryStatusManager: BatteryStatusManager,
    private val cellularStatusManager: CellularStatusManager,
    private val wifiStatusManager: WifiStatusManager,
    private val systemActionsService: SystemActionsService
): ViewModel() {
    private val _dockEditMode = MutableStateFlow(false)
    val dockEditMode: StateFlow<Boolean> = _dockEditMode
    val wifiLevel = wifiStatusManager.level.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val cellularLevel: StateFlow<Int> = cellularStatusManager.level.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val cellularNetworkType: StateFlow<String> = cellularStatusManager.networkType.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val batteryLevel: StateFlow<Int> = batteryStatusManager.level.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val isCharging: StateFlow<Boolean> = batteryStatusManager.isCharging.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val isFullCharge: StateFlow<Boolean> = batteryStatusManager.isFullCharge.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val dock: StateFlow<List<AppShortcutEntity>> = repo.dock.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val home: StateFlow<List<AppShortcutEntity>> = repo.home.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun moveDock(id: Long, newRank: Int) = viewModelScope.launch { repo.moveDock(id, newRank) }

    fun moveHome(id: Long, screen: Int, row: Int, column: Int) = viewModelScope.launch { repo.moveHome(id, screen, row, column) }

    fun remove(id: Long) = viewModelScope.launch { repo.remove(id) }

    fun clearDock() = viewModelScope.launch { repo.clearDock() }

    fun clearHome() = viewModelScope.launch { repo.clearHome() }

    fun getBatteryDrawable(levelBucket: Int, charging: Boolean, isFullCharge: Boolean): Int = batteryStatusManager.getBatteryDrawable(levelBucket, charging, isFullCharge)
    fun getCellularDrawable(level: Int): Int = cellularStatusManager.getCellularDrawableForLevel(level)

    fun getWifiDrawable(level: Int): Int = wifiStatusManager.getWifiDrawableForLevel(level)

    fun openNotifications(): Boolean = systemActionsService.openNotifications()

    fun getAppIcons(pkg: String) = appRepositoryManager.getAppIcon(packageName = pkg)

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
}