package com.paraskcd.influentiallauncher.startmenu.presentation.shared.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paraskcd.influentiallauncher.designsystem.atoms.InfButton
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics

@Composable
fun PermissionPrompt(
    message: String,
    permission: String,
    onResult: () -> Unit,
    modifier: Modifier = Modifier
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { onResult() }
    InfGroupedCard(index = 0, count = 1, modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(StartMenuMetrics.permissionGap),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(StartMenuMetrics.permissionPadding)
        ) {
            Text(text = message, style = MaterialTheme.typography.bodyLarge, color = InfTheme.colors.textPrimary)
            InfButton(label = stringResource(R.string.startmenu_allow), onClick = { launcher.launch(permission) })
        }
    }
}
