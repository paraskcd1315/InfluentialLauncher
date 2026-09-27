package com.paraskcd.influentiallauncher.homescreen.presentation.state

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp

class HomeIconSpots(private val neutral: () -> Offset) {
    val spots = mutableStateMapOf<String, IconSpot>()
    private val shown = mutableStateMapOf<String, Boolean>()

    fun place(app: LauncherApp, bounds: Rect) {
        spots[app.id.key] = IconSpot(app, bounds.translate(neutral()))
    }

    fun remove(key: String) {
        spots.remove(key)
    }

    fun isShown(key: String): Boolean = shown[key] == true

    fun setShown(key: String, value: Boolean) {
        if (value) shown[key] = true else shown.remove(key)
    }
}
