// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.infrastructure.aemet

import com.paraskcd.influentiallauncher.weather.domain.model.CompassPoint

object AemetCompass {
    private val points = mapOf(
        "N" to CompassPoint.N,
        "NE" to CompassPoint.NE,
        "E" to CompassPoint.E,
        "SE" to CompassPoint.SE,
        "S" to CompassPoint.S,
        "SO" to CompassPoint.SW,
        "O" to CompassPoint.W,
        "NO" to CompassPoint.NW
    )

    fun pointOf(code: String?): CompassPoint? = code?.trim()?.uppercase()?.let(points::get)
}
