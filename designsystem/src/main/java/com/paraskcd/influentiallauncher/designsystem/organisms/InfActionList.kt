// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.molecules.InfActionRow
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard

@Composable
fun InfActionList(actions: List<InfAction>, onDismiss: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(DsMetrics.groupGap)) {
        actions.forEachIndexed { index, action ->
            InfGroupedCard(index = index, count = actions.size) {
                InfActionRow(
                    icon = action.icon,
                    label = action.label,
                    tint = action.tint,
                    onClick = {
                        onDismiss()
                        action.run()
                    }
                )
            }
        }
    }
}
