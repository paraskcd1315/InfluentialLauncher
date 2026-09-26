package com.paraskcd.influentiallauncher.clock.presentation

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.clock.presentation.utils.ClockFormat
import com.paraskcd.influentiallauncher.clock.presentation.utils.ClockMetrics
import com.paraskcd.influentiallauncher.clock.presentation.viewmodels.ClockViewModel

@Composable
fun ClockHeader(
    modifier: Modifier = Modifier,
    viewModel: ClockViewModel = hiltViewModel()
) {
    val now by viewModel.now.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val locale = LocalConfiguration.current.locales[0]
    val shadow = Shadow(
        color = Color.Black.copy(alpha = ClockMetrics.shadowAlpha),
        offset = ClockMetrics.shadowOffset,
        blurRadius = ClockMetrics.shadowBlur
    )
    Column(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(start = ClockMetrics.sideInset, end = ClockMetrics.sideInset, top = ClockMetrics.topGap)
    ) {
        Text(
            text = ClockFormat.time(now, locale, DateFormat.is24HourFormat(context)),
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = ClockMetrics.timeSize,
                lineHeight = ClockMetrics.timeLineHeight,
                shadow = shadow
            ),
            color = Color.White
        )
        Spacer(Modifier.height(ClockMetrics.dateGap))
        Text(
            text = ClockFormat.date(now, locale),
            style = MaterialTheme.typography.titleMedium.copy(shadow = shadow),
            color = Color.White.copy(alpha = ClockMetrics.dateAlpha)
        )
    }
}
