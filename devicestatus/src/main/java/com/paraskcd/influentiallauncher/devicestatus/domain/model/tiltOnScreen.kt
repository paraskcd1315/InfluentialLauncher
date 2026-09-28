// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.devicestatus.domain.model

fun tiltOnScreen(x: Float, y: Float, quarterTurns: Int): Tilt = when (quarterTurns and 3) {
    1 -> Tilt(y, -x)
    2 -> Tilt(-x, -y)
    3 -> Tilt(-y, x)
    else -> Tilt(x, y)
}
