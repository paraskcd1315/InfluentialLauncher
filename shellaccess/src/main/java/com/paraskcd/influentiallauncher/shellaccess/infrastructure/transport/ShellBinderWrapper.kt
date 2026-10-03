// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.infrastructure.transport

import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import java.io.FileDescriptor

class ShellBinderWrapper(
    private val original: IBinder,
    private val helper: IBinder
) : IBinder {
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

    override fun getInterfaceDescriptor(): String? = original.interfaceDescriptor
    override fun pingBinder(): Boolean = original.pingBinder()
    override fun isBinderAlive(): Boolean = original.isBinderAlive
    override fun queryLocalInterface(descriptor: String): IInterface? = null
    override fun dump(fd: FileDescriptor, args: Array<out String>?) = original.dump(fd, args)
    override fun dumpAsync(fd: FileDescriptor, args: Array<out String>?) = original.dumpAsync(fd, args)
    override fun linkToDeath(recipient: IBinder.DeathRecipient, flags: Int) = original.linkToDeath(recipient, flags)
    override fun unlinkToDeath(recipient: IBinder.DeathRecipient, flags: Int): Boolean = original.unlinkToDeath(recipient, flags)
}
