package com.paraskcd.influentiallauncher.statusbar.presentation.components

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
import androidx.compose.ui.res.stringResource
import com.paraskcd.influentiallauncher.designsystem.foundation.infPanelSurface
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.statusbar.R
import com.paraskcd.influentiallauncher.statusbar.presentation.model.StatusState
import com.paraskcd.influentiallauncher.statusbar.presentation.utils.StatusBarMetrics
import com.paraskcd.influentiallauncher.statusbar.presentation.utils.StatusIcons
import com.paraskcd.influentiallauncher.windowing.presentation.LocalWindowBlurred

@Composable
fun StatusPill(
    status: StatusState,
    modifier: Modifier = Modifier
) {
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
    Row(
        horizontalArrangement = Arrangement.spacedBy(StatusBarMetrics.iconGap),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .infPanelSurface(RoundedCornerShape(StatusBarMetrics.cornerRadius), blurred = LocalWindowBlurred.current)
            .heightIn(min = StatusBarMetrics.minHeight)
            .padding(horizontal = StatusBarMetrics.paddingHorizontal, vertical = StatusBarMetrics.paddingVertical)
    ) {
        if (status.cellular.available) {
            Icon(
                imageVector = StatusIcons.cellular(status.cellular.level),
                contentDescription = stringResource(R.string.statusbar_cellular),
                tint = tint,
                modifier = Modifier.size(StatusBarMetrics.iconSize)
            )
        }
        Icon(
            imageVector = StatusIcons.wifi(status.wifi),
            contentDescription = wifiDescription,
            tint = tint,
            modifier = Modifier.size(StatusBarMetrics.iconSize)
        )
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
