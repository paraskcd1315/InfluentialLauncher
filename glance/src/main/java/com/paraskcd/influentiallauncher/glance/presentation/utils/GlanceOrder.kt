package com.paraskcd.influentiallauncher.glance.presentation.utils

import com.paraskcd.influentiallauncher.glance.presentation.model.GlanceCard
import com.paraskcd.influentiallauncher.glance.presentation.model.WeatherLoad
import com.paraskcd.influentiallauncher.media.domain.model.NowPlaying
import com.paraskcd.influentiallauncher.timetracking.domain.model.TimeEntry

object GlanceOrder {
    fun of(
        media: NowPlaying?,
        mediaAccess: Boolean,
        timers: List<TimeEntry>,
        weather: WeatherLoad,
        locationAccess: Boolean
    ): List<GlanceCard> = buildList {
        if (media?.playing == true) add(GlanceCard.Media(media))
        timers.forEach { add(GlanceCard.Timer(it)) }
        when {
            !locationAccess -> add(GlanceCard.LocationAccess)
            weather is WeatherLoad.Loading -> add(GlanceCard.Loading)
            weather is WeatherLoad.Ready && weather.weather != null -> add(GlanceCard.Forecast(weather.weather))
        }
        if (media != null && !media.playing) add(GlanceCard.Media(media))
        if (!mediaAccess) add(GlanceCard.MediaAccess)
    }
}
