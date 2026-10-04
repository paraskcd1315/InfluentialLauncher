// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.apps.infrastructure.iconpack

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import com.paraskcd.influentiallauncher.apps.domain.model.IconPack
import com.paraskcd.influentiallauncher.apps.domain.ports.IconPacks
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidIconPacks @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val source: IconPackSource
) : IconPacks {

    override suspend fun installed(): List<IconPack> = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        IconPackIntents.actions
            .flatMap { action -> packageManager.queryIntentActivities(Intent(action), 0) }
            .distinctBy { it.activityInfo.packageName }
            .map { IconPack(it.activityInfo.packageName, it.activityInfo.applicationInfo.loadLabel(packageManager).toString()) }
            .sortedBy { it.label.lowercase() }
    }

    override suspend fun iconNames(pack: String): List<String> = withContext(Dispatchers.IO) { source.iconNames(pack) }

    override suspend fun packIcon(pack: String, drawable: String, sizePx: Int): Bitmap? = withContext(Dispatchers.IO) {
        runCatching { source.named(pack, drawable)?.toBitmap(sizePx, sizePx) }.getOrNull()
    }
}
