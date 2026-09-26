package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.paraskcd.influentiallauncher.calendar.domain.model.CalendarEvent
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.TimelineLayout
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.TimelineMetrics
import com.paraskcd.influentiallauncher.timetracking.domain.model.TimeEntry
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.roundToInt

@Composable
fun DayTimeline(
    date: LocalDate,
    entries: List<TimeEntry>,
    events: List<CalendarEvent>,
    onMove: (TimeEntry, Instant, Instant?) -> Unit,
    onOpenEvent: (CalendarEvent) -> Unit,
    onDay: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = InfTheme.colors
    val density = LocalDensity.current
    val zone = ZoneId.systemDefault()
    val now by produceState(Instant.now()) {
        while (true) {
            delay(TimelineMetrics.nowTickMs)
            value = Instant.now()
        }
    }
    val hourHeight = TimelineMetrics.hourHeight
    val minuteHeight = hourHeight / TimelineMetrics.minutesPerHour
    val minutePx = with(density) { minuteHeight.toPx() }
    val scroll = rememberScrollState()
    val timeFormat = remember { DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT) }
    val today = date == LocalDate.now()

    LaunchedEffect(date) {
        val hour = if (today) (LocalTime.now().hour - TimelineMetrics.leadHours).coerceAtLeast(0) else TimelineMetrics.defaultStartHour
        val lift = with(density) { (TimelineMetrics.labelLift * 2).toPx() }
        scroll.scrollTo((hour * TimelineMetrics.minutesPerHour * minutePx - lift).roundToInt().coerceAtLeast(0))
    }

    var swipe by remember { mutableFloatStateOf(0f) }
    val swipeThreshold = with(density) { TimelineMetrics.swipeThresholdDp.dp.toPx() }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(date) {
                detectHorizontalDragGestures(
                    onDragStart = { swipe = 0f },
                    onDragEnd = {
                        if (swipe <= -swipeThreshold) onDay(1) else if (swipe >= swipeThreshold) onDay(-1)
                        swipe = 0f
                    },
                    onHorizontalDrag = { change, amount ->
                        change.consume()
                        swipe += amount
                    }
                )
            }
            .verticalScroll(scroll)
    ) {
        val contentWidth = maxWidth - TimelineMetrics.gutterWidth - TimelineMetrics.columnGap
        val trackerWidth = contentWidth * TimelineMetrics.trackerShare
        val eventsWidth = contentWidth - trackerWidth - TimelineMetrics.columnGap
        val trackerLeft = TimelineMetrics.gutterWidth + TimelineMetrics.columnGap
        val eventsLeft = trackerLeft + trackerWidth + TimelineMetrics.columnGap
        Box(modifier = Modifier.fillMaxWidth().height(hourHeight * TimelineMetrics.hours + TimelineMetrics.fabSize + TimelineMetrics.fabInset * 2)) {
            repeat(TimelineMetrics.hours) { hour ->
                Box(
                    modifier = Modifier
                        .offset(x = TimelineMetrics.gutterWidth, y = hourHeight * hour)
                        .fillMaxWidth()
                        .height(TimelineMetrics.gridLine)
                        .background(colors.hairline)
                )
                Text(
                    text = LocalTime.of(hour, 0).format(timeFormat),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textTertiary,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier
                        .offset(y = (hourHeight * hour - TimelineMetrics.labelLift).coerceAtLeast(0.dp))
                        .width(TimelineMetrics.gutterWidth)
                        .padding(end = TimelineMetrics.columnGap)
                )
            }

            TimelineLayout.place(events, date, now, { it.begin }, { it.end }).forEach { placed ->
                val event = placed.item
                val laneWidth = laneWidth(eventsWidth, placed.lanes)
                TimelineBlock(
                    title = event.title,
                    subtitle = event.location,
                    time = null,
                    colour = event.colorArgb?.let(::Color) ?: colors.brandText,
                    highlighted = false,
                    modifier = Modifier
                        .offset(x = eventsLeft + (laneWidth + TimelineMetrics.laneGap) * placed.lane, y = minuteHeight * placed.startMinute)
                        .size(laneWidth, minuteHeight * (placed.endMinute - placed.startMinute))
                        .clickable { onOpenEvent(event) }
                )
            }

            TimelineLayout.place(entries, date, now, { it.start }, { it.end }).forEach { placed -> key(placed.item.id) {
                val entry = placed.item
                val laneWidth = laneWidth(trackerWidth, placed.lanes)
                var drag by remember(entry.id, entry.start) { mutableFloatStateOf(0f) }
                var dragging by remember(entry.id) { mutableStateOf(false) }
                val shiftMinutes = TimelineLayout.snap((drag / minutePx).roundToInt())
                val shownStart = entry.start.plusSeconds(shiftMinutes * SecondsPerMinute)
                val shownEnd = entry.end?.plusSeconds(shiftMinutes * SecondsPerMinute)
                val time = "${shownStart.atZone(zone).toLocalTime().format(timeFormat)} – " +
                    (shownEnd?.atZone(zone)?.toLocalTime()?.format(timeFormat) ?: "…")
                TimelineBlock(
                    title = entry.description.ifBlank { entry.projectName.orEmpty() },
                    subtitle = entry.projectName?.takeIf { entry.description.isNotBlank() },
                    time = time,
                    colour = entry.colourArgb?.let(::Color) ?: colors.brand,
                    highlighted = entry.running || dragging,
                    modifier = Modifier
                        .zIndex(if (dragging) 1f else 0f)
                        .offset { IntOffset(0, if (dragging) (shiftMinutes * minutePx).roundToInt() else 0) }
                        .offset(x = trackerLeft + (laneWidth + TimelineMetrics.laneGap) * placed.lane, y = minuteHeight * placed.startMinute)
                        .size(laneWidth, minuteHeight * (placed.endMinute - placed.startMinute))
                        .pointerInput(entry.id, entry.start) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    dragging = true
                                    drag = 0f
                                },
                                onDragEnd = {
                                    val minutes = TimelineLayout.snap((drag / minutePx).roundToInt())
                                    dragging = false
                                    drag = 0f
                                    if (minutes != 0) {
                                        val seconds = minutes * SecondsPerMinute
                                        onMove(entry, entry.start.plusSeconds(seconds), entry.end?.plusSeconds(seconds))
                                    }
                                },
                                onDragCancel = {
                                    dragging = false
                                    drag = 0f
                                },
                                onDrag = { change, amount ->
                                    change.consume()
                                    drag += amount.y
                                }
                            )
                        }
                )
            } }

            if (today) {
                val nowMinute = TimelineLayout.minutesBetween(date.atStartOfDay(zone).toInstant(), now)
                Box(
                    modifier = Modifier
                        .offset(x = TimelineMetrics.gutterWidth, y = minuteHeight * nowMinute - TimelineMetrics.nowLine / 2)
                        .fillMaxWidth()
                        .height(TimelineMetrics.nowLine)
                        .background(colors.danger)
                )
                Box(
                    modifier = Modifier
                        .offset(x = TimelineMetrics.gutterWidth - TimelineMetrics.nowDot / 2, y = minuteHeight * nowMinute - TimelineMetrics.nowDot / 2)
                        .size(TimelineMetrics.nowDot)
                        .clip(CircleShape)
                        .background(colors.danger)
                )
            }
        }
    }
}

private fun laneWidth(columnWidth: Dp, lanes: Int): Dp = (columnWidth - TimelineMetrics.laneGap * (lanes - 1)) / lanes

private const val SecondsPerMinute = 60L
