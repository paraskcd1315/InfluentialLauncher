// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.apps.infrastructure.iconpack

import android.graphics.drawable.Drawable

data class IconPackDecoration(
    val backs: List<Drawable>,
    val mask: Drawable?,
    val upon: Drawable?,
    val scale: Float
)
