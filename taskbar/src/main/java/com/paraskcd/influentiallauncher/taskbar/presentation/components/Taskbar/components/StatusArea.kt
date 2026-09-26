package com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.taskbar.R
import com.paraskcd.influentiallauncher.taskbar.presentation.model.StatusState
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.StatusIcons
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics

@Composable
fun StatusArea(
    status: StatusState,
    modifier: Modifier = Modifier
) {
    val tint = InfTheme.colors.textPrimary
    val batteryDescription = if (status.battery.charging) {
        stringResource(R.string.taskbar_battery_charging, status.battery.percent)
    } else {
        stringResource(R.string.taskbar_battery, status.battery.percent)
    }
    val wifiDescription = if (status.wifi.connected) {
        stringResource(R.string.taskbar_wifi_connected)
    } else {
        stringResource(R.string.taskbar_wifi_off)
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(TaskbarMetrics.statusGap),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        if (status.cellular.available) {
            Icon(
                imageVector = StatusIcons.cellular(status.cellular.level),
                contentDescription = stringResource(R.string.taskbar_cellular),
                tint = tint,
                modifier = Modifier.size(TaskbarMetrics.statusIconSize)
            )
        }
        Icon(
            imageVector = StatusIcons.wifi(status.wifi),
            contentDescription = wifiDescription,
            tint = tint,
            modifier = Modifier.size(TaskbarMetrics.statusIconSize)
        )
        Icon(
            imageVector = StatusIcons.battery(status.battery),
            contentDescription = batteryDescription,
            tint = tint,
            modifier = Modifier.size(TaskbarMetrics.statusIconSize)
        )
        Text(
            text = stringResource(R.string.taskbar_battery_percent, status.battery.percent),
            style = MaterialTheme.typography.labelMedium,
            color = tint
        )
    }
}
