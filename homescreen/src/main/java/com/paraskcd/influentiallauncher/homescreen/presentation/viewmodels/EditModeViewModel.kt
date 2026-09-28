// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.homescreen.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.paraskcd.influentiallauncher.homescreen.domain.usecase.EditMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class EditModeViewModel @Inject constructor(
    private val editMode: EditMode
) : ViewModel() {
    val active: StateFlow<Boolean> = editMode.active

    fun stop() = editMode.stop()
}
