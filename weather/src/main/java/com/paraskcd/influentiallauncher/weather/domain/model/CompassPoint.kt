// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.domain.model

enum class CompassPoint {
    N, NE, E, SE, S, SW, W, NW;

    companion object {
        private const val FullTurn = 360.0
        private const val Step = FullTurn / 8

        fun ofDegrees(degrees: Double): CompassPoint {
            val normalized = ((degrees % FullTurn) + FullTurn) % FullTurn
            return entries[((normalized / Step) + 0.5).toInt() % entries.size]
        }
    }
}
