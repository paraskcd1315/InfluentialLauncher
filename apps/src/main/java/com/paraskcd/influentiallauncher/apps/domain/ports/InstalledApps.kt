package com.paraskcd.influentiallauncher.apps.domain.ports

import android.graphics.Bitmap
import android.graphics.Rect
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import kotlinx.coroutines.flow.Flow

interface InstalledApps {
    val apps: Flow<List<LauncherApp>>

    fun launch(id: AppId, sourceBounds: Rect?): Boolean

    fun openInfo(id: AppId, sourceBounds: Rect?): Boolean

    fun uninstall(id: AppId): Boolean

    suspend fun icon(id: AppId, sizePx: Int, tint: Int?): Bitmap?
}
