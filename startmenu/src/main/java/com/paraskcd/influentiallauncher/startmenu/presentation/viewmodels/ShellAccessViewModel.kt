// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.viewmodels

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.ViewModel
import com.paraskcd.influentiallauncher.shellaccess.domain.model.ShellState
import com.paraskcd.influentiallauncher.shellaccess.domain.ports.ShellAccess
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ShellAccessViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val shellAccess: ShellAccess
) : ViewModel() {
    val state: StateFlow<ShellState> = shellAccess.state

    init {
        shellAccess.ensureReady()
    }

    fun pair(code: String) {
        shellAccess.startPairing(code.trim())
    }

    fun retry() {
        shellAccess.retry()
    }

    fun openWirelessDebugging() {
        val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(intent) }
    }
}
