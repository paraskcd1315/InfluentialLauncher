package com.paraskcd.influentiallauncher.homescreen.presentation.viewmodels

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LaunchOrigin
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.homescreen.domain.usecase.HomeScreen
import com.paraskcd.influentiallauncher.homescreen.domain.usecase.HomeScreenPage
import com.paraskcd.influentiallauncher.homescreen.domain.usecase.HomeScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val home: HomeScreen
) : ViewModel() {

    val state: StateFlow<HomeScreenState?> = home.state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), null)

    private val _wiggling = MutableStateFlow(false)
    val wiggling: StateFlow<Boolean> = _wiggling.asStateFlow()

    private val _overview = MutableStateFlow(false)
    val overview: StateFlow<Boolean> = _overview.asStateFlow()

    private val _removing = MutableStateFlow<LauncherApp?>(null)
    val removing: StateFlow<LauncherApp?> = _removing.asStateFlow()

    private val _deleting = MutableStateFlow<HomeScreenPage?>(null)
    val deleting: StateFlow<HomeScreenPage?> = _deleting.asStateFlow()

    fun startWiggle() {
        _overview.value = false
        _wiggling.value = true
    }

    fun stopWiggle() {
        _wiggling.value = false
    }

    fun openOverview() {
        _wiggling.value = false
        _overview.value = true
    }

    fun closeOverview() {
        _overview.value = false
    }

    fun launch(app: AppId, origin: LaunchOrigin?): Boolean = home.launch(app, origin)

    suspend fun icon(app: AppId, sizePx: Int, tint: Int, background: Int): Bitmap? = home.icon(app, sizePx, tint, background)

    fun move(app: AppId, pageId: String, index: Int) = run { home.move(app, pageId, index) }

    fun moveToNewPage(app: AppId) = run {
        val id = home.addPage()
        home.move(app, id, 0)
    }

    fun addPage() = run { home.addPage() }

    fun setHome(pageId: String) = run { home.setHome(pageId) }

    fun askRemove(app: LauncherApp) {
        _removing.value = app
    }

    fun cancelRemove() {
        _removing.value = null
    }

    fun confirmRemove(app: LauncherApp) {
        _removing.value = null
        run { home.remove(app.id) }
    }

    fun askDelete(page: HomeScreenPage) {
        _deleting.value = page
    }

    fun cancelDelete() {
        _deleting.value = null
    }

    fun confirmDelete(page: HomeScreenPage) {
        _deleting.value = null
        run { home.deletePage(page.id) }
    }

    private fun run(block: suspend () -> Unit) {
        viewModelScope.launch {
            runCatching { block() }.onFailure { Log.w(LogTag, "home edit failed", it) }
        }
    }

    private companion object {
        const val LogTag = "HomeScreen"
        const val StopTimeoutMs = 5_000L
    }
}
