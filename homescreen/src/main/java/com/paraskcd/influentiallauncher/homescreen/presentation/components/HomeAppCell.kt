package com.paraskcd.influentiallauncher.homescreen.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Minus
import com.paraskcd.influentiallauncher.designsystem.theme.LocalWallpaperInk
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.designsystem.atoms.InfAsyncIcon
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.homescreen.R
import com.paraskcd.influentiallauncher.homescreen.presentation.utils.HomeMetrics


@Composable
fun HomeAppCell(
    app: LauncherApp,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    modifier: Modifier = Modifier,
    iconSize: Dp = HomeMetrics.iconSize,
    onRemove: (() -> Unit)? = null
) {
    val ink = LocalWallpaperInk.current
    Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HomeMetrics.labelGap)
        ) {
            Box {
                InfAsyncIcon(
                    key = app.id.key,
                    size = iconSize,
                    load = { loadIcon(app.id, it) },
                    version = loadIcon
                )
                if (onRemove != null) {
                    val label = stringResource(R.string.home_remove_app, app.label)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .size(HomeMetrics.badgeSize)
                            .clip(CircleShape)
                            .background(InfTheme.colors.surfaceContainerHigh)
                            .clickable(onClickLabel = label, onClick = onRemove)
                    ) {
                        Icon(
                            imageVector = Lucide.Minus,
                            contentDescription = label,
                            tint = InfTheme.colors.textPrimary,
                            modifier = Modifier.size(HomeMetrics.badgeGlyph)
                        )
                    }
                }
            }
            Text(
                text = app.label,
                style = MaterialTheme.typography.labelMedium.copy(
                    shadow = Shadow(color = ink.shadow.copy(alpha = HomeMetrics.labelShadowAlpha), blurRadius = HomeMetrics.labelShadowBlur)
                ),
                color = ink.content,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}
