// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.homescreen.presentation.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.Dp
import com.paraskcd.influentiallauncher.designsystem.atoms.InfCountBadge
import com.paraskcd.influentiallauncher.designsystem.atoms.InfOpenBar
import com.paraskcd.influentiallauncher.designsystem.atoms.InfRunningDots
import com.paraskcd.influentiallauncher.homescreen.R
import com.paraskcd.influentiallauncher.homescreen.presentation.utils.HomeMetrics

@Composable
fun BoxScope.IconSignals(badge: Int, openTasks: Int, running: Boolean = false, dotsDrop: Dp = HomeMetrics.signalDotsDrop) {
    if (badge > 0) {
        InfCountBadge(
            count = badge,
            contentDescription = pluralStringResource(R.plurals.home_notification_badge, badge, badge),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = HomeMetrics.signalBadgeNudge, y = -HomeMetrics.signalBadgeNudge)
        )
    }
    val marker = Modifier
        .align(Alignment.BottomCenter)
        .offset(y = dotsDrop)
    if (openTasks > 0) {
        InfOpenBar(modifier = marker)
    } else if (running) {
        InfRunningDots(count = 1, modifier = marker)
    }
}
