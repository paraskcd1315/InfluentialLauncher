package com.paraskcd.influentiallauncher.glance.presentation.model

import com.paraskcd.influentiallauncher.media.domain.model.NowPlaying
import com.paraskcd.influentiallauncher.timetracking.domain.model.TimeEntry
import com.paraskcd.influentiallauncher.weather.domain.model.Weather

sealed interface GlanceCard {
    val key: String

    data class Media(val nowPlaying: NowPlaying) : GlanceCard {
        override val key: String = "media"
    }

    data class Timer(val entry: TimeEntry) : GlanceCard {
        override val key: String = "timer:${entry.tracker}"
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
