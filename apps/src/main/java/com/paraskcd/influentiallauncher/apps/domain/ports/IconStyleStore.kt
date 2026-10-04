// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.apps.domain.ports

import com.paraskcd.influentiallauncher.apps.domain.model.AppIconChoice
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.IconStyle
import kotlinx.coroutines.flow.StateFlow

interface IconStyleStore {
    val style: StateFlow<IconStyle>

    suspend fun setIconPack(packageName: String?)

    suspend fun choose(id: AppId, choice: AppIconChoice?)
}
