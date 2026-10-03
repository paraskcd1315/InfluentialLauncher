// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.utils

import java.time.format.DateTimeFormatter

object WeatherFormats {
    val clock: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val weekDay: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE")
    val weekDayClock: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE HH:mm")
}
