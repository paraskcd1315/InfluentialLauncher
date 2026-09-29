// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.infrastructure.spotlight

object SpotlightPeekProtocol {
    val Packages = listOf("com.paraskcd.spotlightsearch", "com.paraskcd.spotlightsearch.dev")
    const val ActionPeek = "com.paraskcd.spotlightsearch.action.PEEK"
    const val ExtraPeek = "com.paraskcd.spotlightsearch.extra.PEEK"
    const val KeyProgress = "progress"
    const val KeyVelocity = "velocity"
    const val Register = 1
    const val Progress = 2
    const val Commit = 3
    const val Cancel = 4
    const val Closed = 10
}
