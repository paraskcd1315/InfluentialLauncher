// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pause
import com.composables.icons.lucide.Play
import com.composables.icons.lucide.SkipBack
import com.composables.icons.lucide.SkipForward
import com.paraskcd.influentiallauncher.glance.R
import com.paraskcd.influentiallauncher.glance.presentation.utils.GlanceMetrics
import com.paraskcd.influentiallauncher.media.domain.model.NowPlaying

@Composable
fun MediaGlance(
    nowPlaying: NowPlaying,
    onOpen: () -> Unit,
    onPrevious: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(GlanceMetrics.itemGap),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(GlanceMetrics.itemGap),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .clickable(onClickLabel = nowPlaying.title, onClick = onOpen)
        ) {
            val artwork = nowPlaying.artwork
            if (artwork != null) {
                val image = remember(artwork) { artwork.asImageBitmap() }
                Image(
                    bitmap = image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(GlanceMetrics.artwork)
                        .clip(RoundedCornerShape(GlanceMetrics.artworkRadius))
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                GlanceText(text = nowPlaying.title, style = MaterialTheme.typography.titleMedium)
                nowPlaying.artist?.let {
                    GlanceText(text = it, style = MaterialTheme.typography.bodyMedium, alpha = GlanceMetrics.secondaryAlpha)
                }
            }
        }
        GlanceControl(Lucide.SkipBack, stringResource(R.string.glance_previous), onPrevious)
        GlanceControl(
            icon = if (nowPlaying.playing) Lucide.Pause else Lucide.Play,
            description = stringResource(if (nowPlaying.playing) R.string.glance_pause else R.string.glance_play),
            onClick = onPlayPause
        )
        GlanceControl(Lucide.SkipForward, stringResource(R.string.glance_next), onNext)
    }
}
