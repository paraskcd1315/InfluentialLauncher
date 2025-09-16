package com.paraskcd.influentiallauncher.ui.components.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.microsoft.fluent.mobile.icons.R
import com.paraskcd.influentiallauncher.data.types.MediaState
import com.paraskcd.influentiallauncher.viewmodels.MediaViewModel

@Composable
fun MediaWidget(
    modifier: Modifier = Modifier,
    vm: MediaViewModel = hiltViewModel()
) {
    val ctx = LocalContext.current
    val state by vm.state.collectAsState()
    val artShape = RoundedCornerShape(12.dp)

    when (val s = state) {
        is MediaState.Loading -> {
            Box(modifier = modifier.fillMaxWidth().height(148.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is MediaState.PermissionRequired -> {
            Column(
                modifier = modifier.fillMaxWidth().height(148.dp).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("You need permissions to read media playback", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(8.dp))
                Button(onClick = { vm.openNotificationAccessSettings(ctx) }) {
                    Text("Grant Permissions")
                }
            }
        }
        is MediaState.Unavailable -> {
            Box(modifier = modifier.fillMaxWidth().height(148.dp)) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .shadow(12.dp, artShape, clip = false)
                            .clip(artShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = { vm.previous() }, modifier = Modifier.size(40.dp)) {
                            Icon(
                                painter = painterResource(R.drawable.ic_fluent_previous_24_regular),
                                contentDescription = "Previous",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        FilledIconButton(
                            onClick = { vm.playPause() },
                            modifier = Modifier.size(56.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(0.5f),
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(
                                painter = painterResource( R.drawable.ic_fluent_play_24_filled),
                                contentDescription = "Play"
                            )
                        }
                        IconButton(onClick = { vm.next() }, modifier = Modifier.size(40.dp)) {
                            Icon(
                                painter = painterResource(R.drawable.ic_fluent_next_24_regular),
                                contentDescription = "Skip",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
        is MediaState.Ready -> {
            val info = s.info
            Box(modifier = modifier.fillMaxWidth().height(148.dp).clip(RoundedCornerShape(24.dp))) {
                info.artwork?.let { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .matchParentSize()
                            .blur(40.dp)
                            .alpha(0.5f)
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    )
                }
                Row(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        if (info.artwork != null) {
                            Image(
                                bitmap = info.artwork.asImageBitmap(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(72.dp)
                                    .shadow(12.dp, artShape, clip = false)
                                    .clip(artShape)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .shadow(12.dp, artShape, clip = false)
                                    .clip(artShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = info.title ?: "",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = info.artist ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                            )
                            if (!info.album.isNullOrBlank()) {
                                Text(
                                    text = info.album,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = { vm.previous() }, modifier = Modifier.size(40.dp)) {
                            Icon(
                                painter = painterResource(R.drawable.ic_fluent_previous_24_regular),
                                contentDescription = "Previous",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        FilledIconButton(
                            onClick = { vm.playPause() },
                            modifier = Modifier.size(56.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(0.5f),
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            val icon = if (info.isPlaying)
                                R.drawable.ic_fluent_pause_24_filled
                            else
                                R.drawable.ic_fluent_play_24_filled
                            Icon(
                                painter = painterResource(icon),
                                contentDescription = if (info.isPlaying) "Pause" else "Play"
                            )
                        }
                        IconButton(onClick = { vm.next() }, modifier = Modifier.size(40.dp)) {
                            Icon(
                                painter = painterResource(R.drawable.ic_fluent_next_24_regular),
                                contentDescription = "Skip",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}