package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.apps.components

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LaunchOrigin
import com.paraskcd.influentiallauncher.apps.infrastructure.LaunchOrigins
import com.paraskcd.influentiallauncher.designsystem.atoms.InfAsyncIcon
import com.paraskcd.influentiallauncher.designsystem.foundation.InfGroupedCorners
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.presentation.model.StartMenuApp
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppRow(
    entry: StartMenuApp,
    index: Int,
    count: Int,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    onLaunch: (AppId, LaunchOrigin?) -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = InfGroupedCorners.of(index, count)
) {
    val view = LocalView.current
    var iconBounds by remember { mutableStateOf<Rect?>(null) }
    InfGroupedCard(index = index, count = count, modifier = modifier, shape = shape) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(StartMenuMetrics.rowIconGap),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClickLabel = entry.app.label,
                    onClick = { onLaunch(entry.app.id, iconBounds?.let { LaunchOrigins.scaleUp(view, it.toAndroidRectF()) }) },
                    onLongClick = onLongPress
                )
                .padding(StartMenuMetrics.rowPadding)
        ) {
            InfAsyncIcon(
                key = entry.app.id.key,
                size = StartMenuMetrics.rowIconSize,
                load = { loadIcon(entry.app.id, it) },
                version = loadIcon,
                modifier = Modifier.onGloballyPositioned { iconBounds = it.boundsInWindow() }
            )
            Text(
                text = entry.app.label,
                style = MaterialTheme.typography.bodyLarge,
                color = InfTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
