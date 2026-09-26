package com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.paraskcd.influentiallauncher.designsystem.atoms.InfTile
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.taskbar.R

@Composable
fun AddPinButton(
    open: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    InfTile(
        onClick = onClick,
        contentDescription = stringResource(R.string.taskbar_add_pin),
        selected = open,
        modifier = modifier
    ) {
        Icon(
            imageVector = Lucide.Plus,
            contentDescription = stringResource(R.string.taskbar_add_pin),
            tint = InfTheme.colors.textSecondary,
            modifier = Modifier.size(DsMetrics.glyphSize)
        )
    }
}
