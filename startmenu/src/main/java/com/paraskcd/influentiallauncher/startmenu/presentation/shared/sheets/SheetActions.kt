package com.paraskcd.influentiallauncher.startmenu.presentation.shared.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.paraskcd.influentiallauncher.designsystem.molecules.InfActionRow
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics

@Composable
fun SheetActions(actions: List<SheetAction>, onDismiss: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(StartMenuMetrics.rowGap)) {
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
