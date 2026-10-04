// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.apps.domain.ports

import android.graphics.Bitmap
import com.paraskcd.influentiallauncher.apps.domain.model.IconPack

interface IconPacks {
    suspend fun installed(): List<IconPack>

    suspend fun iconNames(pack: String): List<String>

    suspend fun packIcon(pack: String, drawable: String, sizePx: Int): Bitmap?
}
