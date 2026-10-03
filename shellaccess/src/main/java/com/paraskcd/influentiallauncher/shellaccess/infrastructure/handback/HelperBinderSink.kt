// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.infrastructure.handback

import android.os.IBinder

object HelperBinderSink {
    @Volatile
    private var listener: ((IBinder) -> Unit)? = null

    @Volatile
    private var pending: IBinder? = null

    @Synchronized
    fun onBinderReceived(binder: IBinder) {
        val current = listener
        if (current != null) current(binder) else pending = binder
    }

    @Synchronized
    fun listen(block: (IBinder) -> Unit) {
        listener = block
        pending?.let {
            pending = null
            block(it)
        }
    }
}
