package com.paraskcd.influentiallauncher.timetracking.domain.model

data class TrackerProject(
    val id: String,
    val name: String,
    val clientName: String?,
    val colourArgb: Int?
)
