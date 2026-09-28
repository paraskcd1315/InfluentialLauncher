// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.molecules

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paraskcd.influentiallauncher.designsystem.atoms.InfSegment
import com.paraskcd.influentiallauncher.designsystem.foundation.infPanelSurface
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing

@Composable
fun InfSegmented(
    labels: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    blurred: Boolean,
    modifier: Modifier = Modifier,
    equalWidth: Boolean = false
) {
    Box(
        modifier = modifier
            .infPanelSurface(InfShapes.pill, blurred = blurred)
            .padding(InfSpacing.s1)
    ) {
        Row(
            modifier = if (equalWidth) Modifier.fillMaxWidth() else Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(InfSpacing.s1),
            verticalAlignment = Alignment.CenterVertically
        ) {
            labels.forEachIndexed { index, label ->
                InfSegment(
                    label = label,
                    active = index == selected,
                    onClick = { onSelect(index) },
                    modifier = if (equalWidth) Modifier.weight(1f) else Modifier
                )
            }
        }
    }
}
