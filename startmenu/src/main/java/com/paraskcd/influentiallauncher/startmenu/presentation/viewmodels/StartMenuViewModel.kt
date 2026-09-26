package com.paraskcd.influentiallauncher.startmenu.presentation.viewmodels

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.ports.InstalledApps
import com.paraskcd.influentiallauncher.pins.domain.usecase.PinnedApps
import com.paraskcd.influentiallauncher.startmenu.presentation.model.AppSection
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.AppSections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartMenuViewModel @Inject constructor(
    private val installedApps: InstalledApps,
    private val pinnedApps: PinnedApps
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val sections: StateFlow<List<AppSection>?> = combine(installedApps.apps, pinnedApps.pinnedIds, _query) { apps, pins, query ->
        AppSections.of(apps, pins, query)
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), null)

    fun setQuery(value: String) {
        _query.value = value
    }

    fun launch(id: AppId, sourceBounds: Rect?) {
        installedApps.launch(id, sourceBounds)
    }

    fun togglePin(id: AppId) {
        viewModelScope.launch { pinnedApps.toggle(id) }
    }

    fun openInfo(id: AppId, sourceBounds: Rect?) {
        installedApps.openInfo(id, sourceBounds)
    }

    fun uninstall(id: AppId) {
        installedApps.uninstall(id)
    }

    suspend fun icon(id: AppId, sizePx: Int): Bitmap? = installedApps.icon(id, sizePx)

    private companion object {
        const val StopTimeoutMs = 5_000L
    }
}
