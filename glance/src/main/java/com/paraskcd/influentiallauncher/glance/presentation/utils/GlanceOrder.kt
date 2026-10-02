// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.utils

import com.paraskcd.influentiallauncher.glance.presentation.model.GlanceCard
import com.paraskcd.influentiallauncher.glance.presentation.model.WeatherLoad
import com.paraskcd.influentiallauncher.media.domain.model.NowPlaying
import com.paraskcd.influentiallauncher.timetracking.domain.model.Tracker

object GlanceOrder {
    private val alwaysShown = setOf(Tracker.Toggl)

    fun showsIdle(tracker: Tracker): Boolean = tracker in alwaysShown

    fun of(
        media: NowPlaying?,
        mediaAccess: Boolean,
        timers: List<GlanceCard.Timer>,
        weather: WeatherLoad,
        locationAccess: Boolean
    ): List<GlanceCard> = buildList {
        if (media?.playing == true) add(GlanceCard.Media(media))
        addAll(timers.filter { it.entry != null })
        when {
            !locationAccess -> add(GlanceCard.LocationAccess)
            weather is WeatherLoad.Loading -> add(GlanceCard.Loading)
            weather is WeatherLoad.Ready && weather.weather != null -> add(GlanceCard.Forecast(weather.weather))
        }
        addAll(timers.filter { it.entry == null && it.week != null && showsIdle(it.tracker) })
        if (media != null && !media.playing) add(GlanceCard.Media(media))
        if (!mediaAccess) add(GlanceCard.MediaAccess)
    }
}
