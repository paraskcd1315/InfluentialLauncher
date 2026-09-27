package com.paraskcd.influentiallauncher.glance.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.glance.presentation.model.PlacePicker
import com.paraskcd.influentiallauncher.glance.presentation.model.WeatherSheetState
import com.paraskcd.influentiallauncher.weather.domain.model.Place
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherSourceName
import com.paraskcd.influentiallauncher.weather.domain.ports.PlaceSearch
import com.paraskcd.influentiallauncher.weather.domain.ports.SavedPlaces
import com.paraskcd.influentiallauncher.weather.domain.ports.WeatherSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weather: WeatherSource,
    private val savedPlaces: SavedPlaces,
    private val placeSearch: PlaceSearch
) : ViewModel() {

    private val _sheet = MutableStateFlow<WeatherSheetState?>(null)
    val sheet: StateFlow<WeatherSheetState?> = _sheet.asStateFlow()

    private val _picker = MutableStateFlow<PlacePicker?>(null)
    val picker: StateFlow<PlacePicker?> = _picker.asStateFlow()

    private val _removing = MutableStateFlow<Place?>(null)
    val removing: StateFlow<Place?> = _removing.asStateFlow()

    val places: StateFlow<List<Place>> = savedPlaces.places
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), emptyList())

    val selectedPlace: StateFlow<Place?> = savedPlaces.selected
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), null)

    private var loading: Job? = null
    private var searching: Job? = null

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
        _picker.value = null
    }

    fun openPicker() {
        _picker.value = PlacePicker()
    }

    fun toggleAdding() {
        searching?.cancel()
        _picker.update { it?.let { current -> PlacePicker(adding = !current.adding) } }
    }

    fun closePicker() {
        searching?.cancel()
        _picker.value = null
    }

    fun search(query: String) {
        _picker.update { it?.copy(query = query, searching = query.isNotBlank()) }
        searching?.cancel()
        if (query.isBlank()) {
            _picker.update { it?.copy(results = emptyList(), searching = false) }
            return
        }
        searching = viewModelScope.launch {
            delay(SearchDebounceMs)
            val results = runCatching { placeSearch.search(query) }
                .onFailure { Log.w(LogTag, "place search failed", it) }
                .getOrDefault(emptyList())
            _picker.update { it?.copy(results = results, searching = false) }
        }
    }

    fun pick(place: Place?) {
        closePicker()
        viewModelScope.launch {
            savedPlaces.select(place)
            reload()
        }
    }

    fun add(place: Place) {
        closePicker()
        viewModelScope.launch {
            savedPlaces.save(place)
            savedPlaces.select(place)
            reload()
        }
    }

    fun toggleSaved(place: Place) {
        if (places.value.any { it.key() == place.key() }) {
            _removing.value = place
        } else {
            viewModelScope.launch { savedPlaces.save(place) }
        }
    }

    fun askRemove(place: Place) {
        _removing.value = place
    }

    fun cancelRemove() {
        _removing.value = null
    }

    fun confirmRemove(place: Place) {
        _removing.value = null
        viewModelScope.launch {
            val wasSelected = selectedPlace.value?.key() == place.key()
            savedPlaces.remove(place)
            if (wasSelected) reload()
        }
    }

    private fun reload() {
        _sheet.update { it?.copy(selected = null, loading = true) }
        load(null)
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
        const val StopTimeoutMs = 5_000L
        const val SearchDebounceMs = 350L
    }
}
