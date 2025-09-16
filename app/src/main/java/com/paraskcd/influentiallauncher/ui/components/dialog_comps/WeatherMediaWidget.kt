package com.paraskcd.influentiallauncher.ui.components.dialog_comps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paraskcd.influentiallauncher.ui.components.widgets.MediaWidget
import com.paraskcd.influentiallauncher.ui.components.widgets.WeatherWidget

@Composable
fun WeatherMediaWidget(
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    HorizontalPager(state = pagerState, modifier = modifier.fillMaxWidth()) { page ->
        when (page) {
            0 -> WeatherWidget(Modifier.fillMaxWidth())
            else -> MediaWidget(Modifier.fillMaxWidth())
        }
    }
}