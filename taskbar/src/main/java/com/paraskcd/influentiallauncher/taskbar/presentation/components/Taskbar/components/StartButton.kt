package com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paraskcd.influentiallauncher.designsystem.atoms.InfTile
import com.paraskcd.influentiallauncher.designsystem.icons.WindowsLogo
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.taskbar.R
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics

@Composable
fun StartButton(
    open: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    InfTile(
        onClick = onClick,
        contentDescription = stringResource(R.string.taskbar_start),
        selected = open,
        modifier = modifier
    ) {
        Icon(
            imageVector = WindowsLogo,
            contentDescription = stringResource(R.string.taskbar_start),
            tint = InfTheme.colors.brandText,
            modifier = Modifier.size(TaskbarMetrics.startGlyphSize)
        )
    }
}
