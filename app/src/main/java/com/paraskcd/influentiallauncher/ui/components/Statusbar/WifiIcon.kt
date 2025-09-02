package com.paraskcd.influentiallauncher.ui.components.Statusbar

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.paraskcd.influentiallauncher.viewmodels.LauncherItemsViewModel

@Composable
fun WifiIcon(context: Context, viewModel: LauncherItemsViewModel) {
    val wifiLevel = viewModel.wifiLevel.collectAsState()
    val wifiDrawable = viewModel.getWifiDrawable(wifiLevel.value)
    Image(
        painter = rememberDrawablePainter(drawable = ContextCompat.getDrawable(context, wifiDrawable)?.mutate()?.apply {
            colorFilter = PorterDuffColorFilter(MaterialTheme.colorScheme.onSurface.toArgb(), PorterDuff.Mode.SRC_IN)
        }),
        contentDescription = null,
        modifier = Modifier
            .width(36.dp)
            .height(36.dp)
            .padding(6.dp)
    )
}