package com.paraskcd.influentiallauncher.ui.components.Statusbar

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paraskcd.influentiallauncher.viewmodels.LauncherItemsViewModel

@Composable
fun Statusbar(context: Context, viewModel: LauncherItemsViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        CellularData(context, viewModel)
        WifiIcon(context, viewModel)
        BatteryIcon(context, viewModel)
    }
}