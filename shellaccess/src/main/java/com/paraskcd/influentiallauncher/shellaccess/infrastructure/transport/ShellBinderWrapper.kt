// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.infrastructure.transport

import android.os.Binder
import android.os.IBinder
import android.os.Parcel

class ShellBinderWrapper(
    private val original: IBinder,
    private val helper: IBinder
) : Binder() {
    override fun transact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
        val forward = Parcel.obtain()
        try {
            forward.writeInterfaceToken(ShellProtocol.Descriptor)
            forward.writeStrongBinder(original)
            forward.writeInt(code)
            forward.writeInt(flags)
            forward.appendFrom(data, 0, data.dataSize())
            helper.transact(ShellProtocol.TransactionTransactRemote, forward, reply, 0)
        } finally {
            forward.recycle()
        }
        return true
    }
}
