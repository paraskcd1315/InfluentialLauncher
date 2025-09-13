package com.paraskcd.influentiallauncher.data.types

import androidx.compose.runtime.Immutable

@Immutable
data class AppMenuAction(
    val label: String,
    val destructive: Boolean = false,
    val onClick: () -> Unit
)
