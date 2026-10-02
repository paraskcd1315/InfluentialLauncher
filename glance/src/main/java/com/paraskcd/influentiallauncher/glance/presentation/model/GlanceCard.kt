// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.model

import com.paraskcd.influentiallauncher.media.domain.model.NowPlaying
import com.paraskcd.influentiallauncher.timetracking.domain.model.TimeEntry
import com.paraskcd.influentiallauncher.timetracking.domain.model.Tracker
import com.paraskcd.influentiallauncher.weather.domain.model.Weather
import java.time.Duration

sealed interface GlanceCard {
    val key: String

    data class Media(val nowPlaying: NowPlaying) : GlanceCard {
        override val key: String = "media"
    }

    data class Timer(
        val tracker: Tracker,
        val entry: TimeEntry?,
        val week: List<TimeEntry>?,
        val weekTarget: Duration?
    ) : GlanceCard {
        override val key: String = "timer:$tracker"
    }

    data class Forecast(val weather: Weather) : GlanceCard {
        override val key: String = "weather"
    }

    data object MediaAccess : GlanceCard {
        override val key: String = "media:access"
    }

    data object LocationAccess : GlanceCard {
        override val key: String = "weather:access"
    }

    data object Loading : GlanceCard {
        override val key: String = "weather"
    }
}
