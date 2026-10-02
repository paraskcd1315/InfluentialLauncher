// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.statusbar.presentation.utils

import androidx.annotation.StringRes
import com.paraskcd.influentiallauncher.devicestatus.domain.model.MobileDataType
import com.paraskcd.influentiallauncher.statusbar.R

object StatusLabels {
    @StringRes
    fun mobileData(type: MobileDataType): Int = when (type) {
        MobileDataType.G -> R.string.statusbar_data_g
        MobileDataType.E -> R.string.statusbar_data_e
        MobileDataType.OneX -> R.string.statusbar_data_1x
        MobileDataType.ThreeG -> R.string.statusbar_data_3g
        MobileDataType.FourG -> R.string.statusbar_data_4g
        MobileDataType.FourGPlus -> R.string.statusbar_data_4g_plus
        MobileDataType.Lte -> R.string.statusbar_data_lte
        MobileDataType.LtePlus -> R.string.statusbar_data_lte_plus
        MobileDataType.FourGLte -> R.string.statusbar_data_4g_lte
        MobileDataType.FourGLtePlus -> R.string.statusbar_data_4g_lte_plus
        MobileDataType.FiveGE -> R.string.statusbar_data_5g_e
        MobileDataType.FiveG -> R.string.statusbar_data_5g
        MobileDataType.FiveGPlus -> R.string.statusbar_data_5g_plus
    }
}
