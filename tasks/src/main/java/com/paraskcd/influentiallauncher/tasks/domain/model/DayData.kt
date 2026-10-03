// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.tasks.domain.model

data class DayData(
    val dayStart: Long,
    val mobileBytes: Long,
    val wifiBytes: Long
) {
    val totalBytes: Long get() = mobileBytes + wifiBytes
}
