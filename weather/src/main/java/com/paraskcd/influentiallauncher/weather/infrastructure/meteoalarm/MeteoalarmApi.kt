package com.paraskcd.influentiallauncher.weather.infrastructure.meteoalarm

object MeteoalarmApi {
    const val BaseUrl = "https://feeds.meteoalarm.org/api/v1"

    object Paths {
        const val SpainWarnings = "/warnings/feeds-spain"
    }

    object Fields {
        const val Warnings = "warnings"
        const val Alert = "alert"
        const val MessageType = "msgType"
        const val Info = "info"
        const val Language = "language"
        const val Area = "area"
        const val AreaName = "areaDesc"
        const val Headline = "headline"
        const val Description = "description"
        const val Onset = "onset"
        const val Expires = "expires"
        const val Parameter = "parameter"
        const val ParameterName = "valueName"
        const val ParameterValue = "value"
        const val AwarenessLevel = "awareness_level"
        const val Cancel = "Cancel"
    }
}
