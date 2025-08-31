package com.paraskcd.influentiallauncher.ui.theme.components.Statusbar

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.paraskcd.influentiallauncher.ui.theme.viewmodels.LauncherItemsViewModel

@Composable
fun CellularData(context: Context, viewModel: LauncherItemsViewModel) {
    val cellularLevel = viewModel.cellularLevel.collectAsState()
    val networkType = viewModel.cellularNetworkType.collectAsState()
    val cellularDrawable = viewModel.getCellularDrawable(cellularLevel.value)
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = networkType.value, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodySmall)
        Image(
            painter = rememberDrawablePainter(drawable = ContextCompat.getDrawable(context, cellularDrawable)?.mutate()?.apply {
                colorFilter = PorterDuffColorFilter(MaterialTheme.colorScheme.onSurface.toArgb(), PorterDuff.Mode.SRC_IN)
            }),
            contentDescription = null,
            modifier = Modifier
                .width(48.dp)
                .height(48.dp)
                .padding(6.dp)
        )
    }
}