// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.homescreen.presentation.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.apps.domain.model.AppIconChoice
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.IconPack
import com.paraskcd.influentiallauncher.apps.domain.model.IconStyle
import com.paraskcd.influentiallauncher.apps.domain.ports.IconPacks
import com.paraskcd.influentiallauncher.apps.domain.ports.IconStyleStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppIconViewModel @Inject constructor(
    private val packs: IconPacks,
    private val styleStore: IconStyleStore
) : ViewModel() {

    val style: StateFlow<IconStyle> = styleStore.style

    private val _packList = MutableStateFlow<List<IconPack>?>(null)
    val packList: StateFlow<List<IconPack>?> = _packList.asStateFlow()

    private val _pack = MutableStateFlow<String?>(null)
    val pack: StateFlow<String?> = _pack.asStateFlow()

    private val _names = MutableStateFlow<List<String>?>(null)
    val names: StateFlow<List<String>?> = _names.asStateFlow()

    private var reading: Job? = null

    fun open(app: AppId) {
        viewModelScope.launch {
            val list = packs.installed()
            _packList.value = list
            val wanted = style.value.choices[app]?.iconPack ?: style.value.iconPack
            select((list.firstOrNull { it.packageName == wanted } ?: list.firstOrNull())?.packageName)
        }
    }

    fun select(pack: String?) {
        _pack.value = pack
        _names.value = null
        reading?.cancel()
        if (pack == null) {
            _names.value = emptyList()
            return
        }
        reading = viewModelScope.launch { _names.value = packs.iconNames(pack) }
    }

    suspend fun packIcon(pack: String, drawable: String, sizePx: Int): Bitmap? = packs.packIcon(pack, drawable, sizePx)

    fun choose(app: AppId, drawable: String) {
        val pack = _pack.value ?: return
        viewModelScope.launch { styleStore.choose(app, AppIconChoice(pack, drawable)) }
    }

    fun reset(app: AppId) {
        viewModelScope.launch { styleStore.choose(app, null) }
    }
}
