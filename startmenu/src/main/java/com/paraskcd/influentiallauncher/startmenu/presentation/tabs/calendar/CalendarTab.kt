package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.model.PermissionState
import com.paraskcd.influentiallauncher.startmenu.presentation.shared.components.ListSkeleton
import com.paraskcd.influentiallauncher.startmenu.presentation.shared.components.PermissionPrompt
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar.components.DayHeader
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar.components.EventRow
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.ListKeys
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics
import com.paraskcd.influentiallauncher.startmenu.presentation.viewmodels.CalendarViewModel

@Composable
fun CalendarTab(
    open: Boolean,
    onClose: () -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val permission by viewModel.permissionState.collectAsStateWithLifecycle()
    val day by viewModel.day.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle()

    LaunchedEffect(open) {
        if (open) viewModel.refreshPermission() else viewModel.today()
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(StartMenuMetrics.rowGap),
        contentPadding = PaddingValues(
            start = StartMenuMetrics.listPadding,
            end = StartMenuMetrics.listPadding,
            top = StartMenuMetrics.listTopPlain,
            bottom = StartMenuMetrics.listBottom
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        if (permission == PermissionState.Missing) {
            item(key = ListKeys.PermissionPrompt) {
                PermissionPrompt(
                    message = stringResource(R.string.startmenu_calendar_permission),
                    permission = viewModel.permission,
                    onResult = viewModel::refreshPermission
                )
            }
            return@LazyColumn
        }
        item(key = ListKeys.DayHeader) {
            DayHeader(day = day, onPrevious = viewModel::previousDay, onNext = viewModel::nextDay, onToday = viewModel::today)
        }
        val current = events
        when {
            current == null -> item { ListSkeleton() }
            current.isEmpty() -> item {
                Text(
                    text = stringResource(R.string.startmenu_no_events),
                    style = MaterialTheme.typography.bodyLarge,
                    color = InfTheme.colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(StartMenuMetrics.listPadding)
                )
            }
            else -> itemsIndexed(current, key = { _, event -> "${event.id}:${event.begin.toEpochMilli()}" }) { index, event ->
                EventRow(
                    event = event,
                    index = index,
                    count = current.size,
                    onOpen = {
                        onClose()
                        viewModel.open(it)
                    }
                )
            }
        }
    }
}
