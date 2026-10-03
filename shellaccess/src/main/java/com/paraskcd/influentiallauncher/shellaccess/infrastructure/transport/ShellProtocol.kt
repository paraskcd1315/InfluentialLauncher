// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.infrastructure.transport

import android.os.IBinder

object ShellProtocol {
    const val Descriptor = "com.paraskcd.influentiallauncher.shellaccess.IInfShell"

    const val TransactionTransactRemote = IBinder.FIRST_CALL_TRANSACTION + 100

    const val ResultCode = "code"
    const val ResultOut = "out"
    const val ResultErr = "err"

    const val HandbackMethod = "sendBinder"
    const val HandbackExtraBinder = "com.paraskcd.influentiallauncher.shellaccess.extra.BINDER"
    const val HandbackAuthoritySuffix = ".shell"
}
