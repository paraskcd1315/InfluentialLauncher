// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.paraskcd.influentiallauncher.designsystem.atoms.InfButton
import com.paraskcd.influentiallauncher.designsystem.atoms.InfSectionHeader
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.designsystem.molecules.InfSettingsRow
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.shellaccess.domain.model.ShellState
import com.paraskcd.influentiallauncher.startmenu.R

@Composable
fun ShellAccessSection(
    state: ShellState,
    onStartPairing: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { onStartPairing() }
    val ready = state is ShellState.Ready
    val failed = state is ShellState.Failed
    Column(
        verticalArrangement = Arrangement.spacedBy(InfSpacing.s3),
        modifier = modifier.fillMaxWidth()
    ) {
        InfSectionHeader(text = stringResource(R.string.startmenu_shell_section))
        InfGroupedCard(index = 0, count = 1) {
            InfSettingsRow(
                label = stringResource(stateLabel(state)),
                caption = stringResource(R.string.startmenu_shell_caption)
            )
        }
        if (!ready) {
            InfButton(
                label = stringResource(R.string.startmenu_shell_pair),
                onClick = {
                    if (notificationsAllowed(context)) onStartPairing() else launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (failed) {
            InfButton(
                label = stringResource(R.string.startmenu_shell_retry),
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun notificationsAllowed(context: Context): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

private fun stateLabel(state: ShellState): Int = when (state) {
    ShellState.NotPaired -> R.string.startmenu_shell_state_not_paired
    ShellState.Pairing -> R.string.startmenu_shell_state_pairing
    ShellState.WaitingForWifi -> R.string.startmenu_shell_state_waiting_wifi
    ShellState.Starting -> R.string.startmenu_shell_state_starting
    ShellState.Ready -> R.string.startmenu_shell_state_ready
    is ShellState.Failed -> R.string.startmenu_shell_state_failed
}
