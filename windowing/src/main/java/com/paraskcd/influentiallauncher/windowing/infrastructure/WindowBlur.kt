package com.paraskcd.influentiallauncher.windowing.infrastructure

import android.content.Context
import android.view.WindowManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.function.Consumer

object WindowBlur {
    fun available(context: Context): Flow<Boolean> = callbackFlow {
        val windowManager = context.getSystemService(WindowManager::class.java)
        if (windowManager == null) {
            trySend(false)
            close()
            return@callbackFlow
        }
        val listener = Consumer<Boolean> { enabled -> trySend(enabled) }
        windowManager.addCrossWindowBlurEnabledListener(ContextCompat.getMainExecutor(context), listener)
        awaitClose { windowManager.removeCrossWindowBlurEnabledListener(listener) }
    }.distinctUntilChanged()
}
