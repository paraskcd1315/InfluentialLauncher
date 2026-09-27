package com.paraskcd.influentiallauncher.startmenu.presentation.model

import androidx.annotation.StringRes

data class TabToggle(
    val tab: StartMenuTab,
    @StringRes val labelRes: Int,
    val shown: Boolean
)
