// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.homescreen.presentation.sheets

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.designsystem.atoms.InfButton
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.homescreen.R
import com.paraskcd.influentiallauncher.homescreen.presentation.components.WeeklyDataChart
import com.paraskcd.influentiallauncher.homescreen.presentation.utils.Bytes
import com.paraskcd.influentiallauncher.homescreen.presentation.utils.UsageAccess
import com.paraskcd.influentiallauncher.tasks.domain.model.DayData
import com.paraskcd.influentiallauncher.windowing.presentation.InfSheetWindow

@Composable
fun DataUsageSheet(
    target: AppId?,
    label: String,
    load: suspend (AppId) -> List<DayData>,
    onDismiss: () -> Unit
) {
    val colors = InfTheme.colors
    val context = LocalContext.current
    InfSheetWindow(
        item = target,
        title = { label },
        onDismiss = onDismiss
    ) { current ->
        val allowed = remember(current.key) { UsageAccess.granted(context) }
        if (!allowed) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(InfSpacing.s3)
            ) {
                Text(
                    text = stringResource(R.string.home_data_grant_text),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
                InfButton(
                    label = stringResource(R.string.home_data_grant),
                    onClick = {
                        runCatching {
                            context.startActivity(
                                Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            return@InfSheetWindow
        }
        var data by remember(current.key) { mutableStateOf<List<DayData>?>(null) }
        LaunchedEffect(current.key) { data = load(current) }
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
                    text = stringResource(R.string.home_data_week),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
                WeeklyDataChart(days = days, modifier = Modifier.padding(top = InfSpacing.s3))
            }
        }
    }
}

private val LoadingHeight = 180.dp
