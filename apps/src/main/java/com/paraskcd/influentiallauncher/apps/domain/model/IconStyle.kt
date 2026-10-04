// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.apps.domain.model

data class IconStyle(
    val iconPack: String? = null,
    val choices: Map<AppId, AppIconChoice> = emptyMap()
)
