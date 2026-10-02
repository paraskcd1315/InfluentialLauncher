// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.statusbar.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.paraskcd.influentiallauncher.devicestatus.domain.model.SignalLevel
import com.paraskcd.influentiallauncher.statusbar.R
import com.paraskcd.influentiallauncher.statusbar.presentation.utils.StatusBarMetrics
import com.paraskcd.influentiallauncher.statusbar.presentation.utils.StatusIcons

@Composable
fun CellularSignal(sims: List<SignalLevel>, tint: Color) {
    val primary = sims.firstOrNull() ?: return
    val secondary = sims.getOrNull(1)
    if (secondary == null) {
        Icon(
            imageVector = StatusIcons.cellular(primary),
            contentDescription = stringResource(R.string.statusbar_cellular),
            tint = tint,
            modifier = Modifier.size(StatusBarMetrics.iconSize)
        )
    } else {
        StackedSignal(
            primaryBars = StatusIcons.bars(primary),
            secondaryBars = StatusIcons.bars(secondary),
            color = tint,
            contentDescription = stringResource(R.string.statusbar_cellular_two_sims)
        )
    }
}
