// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.devicestatus.infrastructure

internal data class SimSetup(
    val subscriptions: List<Int>,
    val dataSubscription: Int,
    val phoneStateGranted: Boolean
)
