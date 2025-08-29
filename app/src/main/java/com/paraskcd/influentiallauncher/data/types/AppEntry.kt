package com.paraskcd.influentiallauncher.data.types

import android.graphics.drawable.Drawable

data class AppEntry(
    val label: String,
    val packageName: String,
    val activityName: String,
    val icon: Drawable?,
    val onClick: () -> Unit
)
