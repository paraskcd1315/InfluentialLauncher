// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.apps.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.model.StartMenuApp
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.apps.components.WeeklyDataChart
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.Bytes
import com.paraskcd.influentiallauncher.tasks.domain.model.DayData
import com.paraskcd.influentiallauncher.windowing.presentation.InfSheetWindow

@Composable
fun DataUsageSheet(
    entry: StartMenuApp?,
    load: suspend (AppId) -> List<DayData>,
    onDismiss: () -> Unit
) {
    val colors = InfTheme.colors
    InfSheetWindow(
        item = entry,
        title = { it.app.label },
        onDismiss = onDismiss
    ) { current ->
        var data by remember(current.app.id.key) { mutableStateOf<List<DayData>?>(null) }
        LaunchedEffect(current.app.id.key) { data = load(current.app.id) }
        val days = data
        if (days == null) {
            Box(modifier = Modifier.fillMaxWidth().height(LoadingHeight))
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(InfSpacing.s2)
            ) {
                Text(
                    text = Bytes.format(days.sumOf { it.totalBytes }),
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.textPrimary
                )
                Text(
                    text = stringResource(R.string.startmenu_data_week),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
                WeeklyDataChart(days = days, modifier = Modifier.padding(top = InfSpacing.s3))
            }
        }
    }
}

private val LoadingHeight = 180.dp
