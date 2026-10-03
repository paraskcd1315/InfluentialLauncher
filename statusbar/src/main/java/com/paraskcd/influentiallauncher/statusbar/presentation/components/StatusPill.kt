// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.statusbar.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.paraskcd.influentiallauncher.designsystem.foundation.infPanelSurface
import com.paraskcd.influentiallauncher.designsystem.foundation.infParallaxLayer
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.statusbar.R
import com.paraskcd.influentiallauncher.statusbar.presentation.model.StatusState
import com.paraskcd.influentiallauncher.statusbar.presentation.utils.StatusBarMetrics
import com.paraskcd.influentiallauncher.statusbar.presentation.utils.StatusIcons
import com.paraskcd.influentiallauncher.statusbar.presentation.utils.StatusLabels
import com.paraskcd.influentiallauncher.windowing.presentation.LocalWindowBlurred

@Composable
fun StatusPill(
    status: StatusState,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    onClick: () -> Unit = {}
) {
    val shape = RoundedCornerShape(StatusBarMetrics.cornerRadius)
    val tint = InfTheme.colors.textPrimary
    val batteryDescription = if (status.battery.charging) {
        stringResource(R.string.statusbar_battery_charging, status.battery.percent)
    } else {
        stringResource(R.string.statusbar_battery, status.battery.percent)
    }
    val wifiDescription = if (status.wifi.connected) {
        stringResource(R.string.statusbar_wifi_connected)
    } else {
        stringResource(R.string.statusbar_wifi_off)
    }
    val mobileData = status.mobileData
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .infPanelSurface(shape, blurred = LocalWindowBlurred.current)
            .background(if (active) InfTheme.colors.brandTint else Color.Transparent, shape)
            .clickable(onClickLabel = stringResource(R.string.statusbar_open_controls), onClick = onClick)
            .heightIn(min = StatusBarMetrics.minHeight)
            .padding(horizontal = StatusBarMetrics.paddingHorizontal, vertical = StatusBarMetrics.paddingVertical)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(StatusBarMetrics.iconGap),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.infParallaxLayer()
        ) {
            if (status.vpn) {
                Icon(
                    imageVector = StatusIcons.vpn,
                    contentDescription = stringResource(R.string.statusbar_vpn),
                    tint = tint,
                    modifier = Modifier.size(StatusBarMetrics.iconSize)
                )
            }
            CellularSignal(sims = status.cellular.sims, tint = tint)
            if (mobileData != null) {
                val label = stringResource(StatusLabels.mobileData(mobileData))
                val description = stringResource(R.string.statusbar_mobile_data, label)
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = tint,
                    modifier = Modifier.semantics { contentDescription = description }
                )
            } else {
                Icon(
                    imageVector = StatusIcons.wifi(status.wifi),
                    contentDescription = wifiDescription,
                    tint = tint,
                    modifier = Modifier.size(StatusBarMetrics.iconSize)
                )
            }
            Icon(
                imageVector = StatusIcons.battery(status.battery),
                contentDescription = batteryDescription,
                tint = tint,
                modifier = Modifier.size(StatusBarMetrics.iconSize)
            )
            Text(
                text = stringResource(R.string.statusbar_battery_percent, status.battery.percent),
                style = MaterialTheme.typography.labelLarge,
                color = tint
            )
        }
    }
}
