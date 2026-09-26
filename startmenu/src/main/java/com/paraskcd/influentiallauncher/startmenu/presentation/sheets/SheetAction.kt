package com.paraskcd.influentiallauncher.startmenu.presentation.sheets

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class SheetAction(
    val icon: ImageVector,
    val label: String,
    val tint: Color,
    val run: () -> Unit
)
