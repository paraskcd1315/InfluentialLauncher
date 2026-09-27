package com.paraskcd.influentiallauncher.glance.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.glance.presentation.model.WeatherSheetState
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherSourceName
import com.paraskcd.influentiallauncher.weather.domain.ports.WeatherSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weather: WeatherSource
) : ViewModel() {

    private val _sheet = MutableStateFlow<WeatherSheetState?>(null)
    val sheet: StateFlow<WeatherSheetState?> = _sheet.asStateFlow()

    private var loading: Job? = null

    fun open() {
        _sheet.value = WeatherSheetState()
        load(null)
    }

    fun select(source: WeatherSourceName) {
        if (_sheet.value?.selected == source) return
        _sheet.update { it?.copy(selected = source, loading = true) }
        load(source)
    }

    fun dismiss() {
        loading?.cancel()
        _sheet.value = null
    }

    private fun load(source: WeatherSourceName?) {
        loading?.cancel()
        loading = viewModelScope.launch {
            val report = runCatching { weather.report(source) }
                .onFailure { Log.w(LogTag, "weather report failed", it) }
                .getOrNull()
            _sheet.update { state ->
                state?.copy(
                    report = report ?: state.report,
                    selected = report?.forecast?.source ?: state.selected,
                    loading = false
                )
            }
        }
    }

    private companion object {
        const val LogTag = "WeatherSheet"
    }
}
