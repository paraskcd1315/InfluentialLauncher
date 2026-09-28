// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.apps.domain.model

import android.graphics.Rect
import android.os.Bundle

data class LaunchOrigin(
    val bounds: Rect,
    val options: Bundle?
)
