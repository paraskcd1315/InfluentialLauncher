package com.paraskcd.influentiallauncher.glance.presentation.utils

import com.paraskcd.influentiallauncher.glance.presentation.model.GlanceCard
import com.paraskcd.influentiallauncher.media.domain.model.NowPlaying
import com.paraskcd.influentiallauncher.timetracking.domain.model.TimeEntry
import com.paraskcd.influentiallauncher.weather.domain.model.Weather

object GlanceOrder {
    fun of(
        media: NowPlaying?,
        mediaAccess: Boolean,
        timers: List<TimeEntry>,
        weather: Weather?,
        locationAccess: Boolean
    ): List<GlanceCard> = buildList {
        if (media?.playing == true) add(GlanceCard.Media(media))
        timers.forEach { add(GlanceCard.Timer(it)) }
        if (weather != null) add(GlanceCard.Forecast(weather)) else if (!locationAccess) add(GlanceCard.LocationAccess)
        if (media != null && !media.playing) add(GlanceCard.Media(media))
        if (!mediaAccess) add(GlanceCard.MediaAccess)
    }
}
