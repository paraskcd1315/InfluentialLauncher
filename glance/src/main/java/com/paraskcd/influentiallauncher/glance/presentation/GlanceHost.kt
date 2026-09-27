package com.paraskcd.influentiallauncher.glance.presentation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.glance.R
import com.paraskcd.influentiallauncher.glance.presentation.components.GlanceText
import com.paraskcd.influentiallauncher.glance.presentation.components.MediaGlance
import com.paraskcd.influentiallauncher.glance.presentation.components.TimerGlance
import com.paraskcd.influentiallauncher.glance.presentation.components.WeatherGlance
import com.paraskcd.influentiallauncher.glance.presentation.model.GlanceCard
import com.paraskcd.influentiallauncher.glance.presentation.utils.GlanceMetrics
import com.paraskcd.influentiallauncher.glance.presentation.sheets.WeatherSheet
import com.paraskcd.influentiallauncher.glance.presentation.viewmodels.GlanceViewModel
import com.paraskcd.influentiallauncher.glance.presentation.viewmodels.WeatherViewModel

@Composable
fun GlanceHost(
    modifier: Modifier = Modifier,
    horizontalInset: Dp = 0.dp,
    viewModel: GlanceViewModel = hiltViewModel(),
    weatherViewModel: WeatherViewModel = hiltViewModel()
) {
    val cards by viewModel.cards.collectAsStateWithLifecycle()
    val weatherSheet by weatherViewModel.sheet.collectAsStateWithLifecycle()
    WeatherSheet(state = weatherSheet, onSelect = weatherViewModel::select, onDismiss = weatherViewModel::dismiss)
    val locationLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { viewModel.refresh() }
    LifecycleResumeEffect(Unit) {
        viewModel.refresh()
        onPauseOrDispose { }
    }
    if (cards.isEmpty()) return
    val pager = rememberPagerState { cards.size }
    val leading = cards.first().key
    LaunchedEffect(leading) { pager.animateScrollToPage(0) }

    Column(modifier = modifier.fillMaxWidth().padding(top = GlanceMetrics.topGap)) {
        HorizontalPager(
            state = pager,
            key = { cards.getOrNull(it)?.key ?: it },
            contentPadding = PaddingValues(horizontal = horizontalInset),
            pageSpacing = horizontalInset,
            modifier = Modifier
                .fillMaxWidth()
                .height(GlanceMetrics.height)
        ) { page ->
            Box(contentAlignment = Alignment.CenterStart) {
                when (val card = cards.getOrNull(page)) {
                    is GlanceCard.Media -> MediaGlance(
                        nowPlaying = card.nowPlaying,
                        onOpen = viewModel::openMedia,
                        onPrevious = viewModel::previous,
                        onPlayPause = viewModel::playPause,
                        onNext = viewModel::next
                    )
                    is GlanceCard.Timer -> TimerGlance(entry = card.entry, onStop = { viewModel.stop(card.entry) })
                    is GlanceCard.Forecast -> WeatherGlance(weather = card.weather, onOpen = weatherViewModel::open)
                    GlanceCard.MediaAccess -> GlanceText(
                        text = stringResource(R.string.glance_media_access),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable(onClick = viewModel::requestMediaAccess)
                    )
                    GlanceCard.LocationAccess -> GlanceText(
                        text = stringResource(R.string.glance_location_access),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable { locationLauncher.launch(viewModel.locationPermission) }
                    )
                    null -> Unit
                }
            }
        }
        if (cards.size > 1) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(GlanceMetrics.dotGap),
                modifier = Modifier.padding(start = horizontalInset)
            ) {
                repeat(cards.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(GlanceMetrics.dot)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = if (index == pager.currentPage) 1f else GlanceMetrics.dotIdleAlpha))
                    )
                }
            }
        }
    }
}
