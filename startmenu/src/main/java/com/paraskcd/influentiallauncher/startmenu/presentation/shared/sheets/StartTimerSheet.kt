package com.paraskcd.influentiallauncher.startmenu.presentation.shared.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.paraskcd.influentiallauncher.designsystem.atoms.InfButton
import com.paraskcd.influentiallauncher.designsystem.atoms.InfSectionHeader
import com.paraskcd.influentiallauncher.designsystem.atoms.InfTextField
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.TimelineMetrics
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.TrackerLabels
import com.paraskcd.influentiallauncher.timetracking.domain.model.StartTimer
import com.paraskcd.influentiallauncher.timetracking.domain.model.Tracker
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerActivity
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerProject
import com.paraskcd.influentiallauncher.windowing.presentation.InfSheetWindow

@Composable
fun StartTimerSheet(
    tracker: Tracker?,
    loadProjects: suspend (Tracker) -> List<TrackerProject>,
    loadActivities: suspend (Tracker, String?) -> List<TrackerActivity>,
    onStart: (Tracker, StartTimer) -> Unit,
    onDismiss: () -> Unit
) {
    InfSheetWindow(
        item = tracker,
        title = { stringResource(R.string.startmenu_timer_title, stringResource(TrackerLabels.nameOf(it))) },
        onDismiss = onDismiss
    ) { current ->
        var description by remember(current) { mutableStateOf("") }
        var projectId by remember(current) { mutableStateOf<String?>(null) }
        var activityId by remember(current) { mutableStateOf<String?>(null) }
        val projects by produceState<List<TrackerProject>?>(null, current) { value = loadProjects(current) }
        val needsActivity = current == Tracker.Kimai
        val activities by produceState<List<TrackerActivity>?>(null, current, projectId) {
            value = if (needsActivity && projectId != null) loadActivities(current, projectId) else emptyList()
        }
        val ready = if (needsActivity) projectId != null && activityId != null else description.isNotBlank() || projectId != null

        Column(verticalArrangement = Arrangement.spacedBy(InfSpacing.s3)) {
            InfTextField(
                value = description,
                onValueChange = { description = it },
                label = stringResource(R.string.startmenu_timer_description),
                placeholder = stringResource(R.string.startmenu_timer_placeholder)
            )
            Column(
                modifier = Modifier
                    .heightIn(max = TimelineMetrics.sheetListMax)
                    .verticalScroll(rememberScrollState())
            ) {
                InfSectionHeader(text = stringResource(R.string.startmenu_timer_project))
                val projectOptions = buildList {
                    if (!needsActivity) add(Option(null, stringResource(R.string.startmenu_timer_no_project), null))
                    projects.orEmpty().forEach { add(Option(it.id, listOfNotNull(it.name, it.clientName).joinToString(" · "), it.colourArgb)) }
                }
                OptionList(
                    options = projectOptions,
                    selected = projectId,
                    loading = projects == null,
                    onSelect = {
                        projectId = it
                        activityId = null
                    }
                )
                if (needsActivity && projectId != null) {
                    InfSectionHeader(text = stringResource(R.string.startmenu_timer_activity))
                    OptionList(
                        options = activities.orEmpty().map { Option(it.id, it.name, null) },
                        selected = activityId,
                        loading = activities == null,
                        onSelect = { activityId = it }
                    )
                }
            }
            InfButton(
                label = stringResource(R.string.startmenu_timer_start),
                enabled = ready,
                onClick = {
                    onStart(current, StartTimer(description.trim(), projectId, activityId))
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private data class Option(val id: String?, val label: String, val colourArgb: Int?)

@Composable
private fun OptionList(
    options: List<Option>,
    selected: String?,
    loading: Boolean,
    onSelect: (String?) -> Unit
) {
    val colors = InfTheme.colors
    if (loading) {
        Text(
            text = stringResource(R.string.startmenu_timer_loading),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
            modifier = Modifier.padding(InfSpacing.s3)
        )
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(StartMenuMetrics.rowGap)) {
        options.forEachIndexed { index, option ->
            InfGroupedCard(index = index, count = options.size) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(InfSpacing.s3),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(option.id) }
                        .padding(InfSpacing.s4)
                ) {
                    Box(
                        modifier = Modifier
                            .size(TimelineMetrics.chipDot)
                            .clip(CircleShape)
                            .background(option.colourArgb?.let(::Color) ?: colors.textTertiary)
                    )
                    Text(
                        text = option.label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (option.id == selected) {
                        Icon(imageVector = Lucide.Check, contentDescription = null, tint = colors.brandText, modifier = Modifier.size(TimelineMetrics.chipHeight / 2))
                    }
                }
            }
        }
    }
}
