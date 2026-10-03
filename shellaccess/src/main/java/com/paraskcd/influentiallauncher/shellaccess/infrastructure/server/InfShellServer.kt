// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.infrastructure.server

import android.content.AttributionSource
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.os.Parcel
import com.paraskcd.influentiallauncher.shellaccess.IInfShell
import com.paraskcd.influentiallauncher.shellaccess.infrastructure.transport.BinderContainer
import com.paraskcd.influentiallauncher.shellaccess.infrastructure.transport.ShellProtocol
import java.io.File

object InfShellServer {
    @JvmStatic
    fun main(args: Array<String>) {
        val packageName = args[0]
        val ownerUid = args[1].toInt()
        val sourceDir = args[2]

        Looper.prepareMainLooper()
        val binder = buildBinder(ownerUid)
        watchForUninstall(sourceDir)
        deliver(packageName, ownerUid, binder)
        Looper.loop()
    }

    private fun buildBinder(ownerUid: Int): IBinder = object : IInfShell.Stub() {
        override fun version(): Int = ServerVersion

        override fun run(command: String): Bundle {
            requireOwner()
            val process = ProcessBuilder("sh", "-c", command).redirectErrorStream(false).start()
            val out = process.inputStream.bufferedReader().readText()
            val err = process.errorStream.bufferedReader().readText()
            val code = process.waitFor()
            return Bundle().apply {
                putInt(ShellProtocol.ResultCode, code)
                putString(ShellProtocol.ResultOut, out)
                putString(ShellProtocol.ResultErr, err)
            }
        }

        override fun exit() {
            requireOwner()
            System.exit(0)
        }

        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            if (code == ShellProtocol.TransactionTransactRemote) {
                requireOwner()
                data.enforceInterface(ShellProtocol.Descriptor)
                val target = data.readStrongBinder()
                val targetCode = data.readInt()
                val targetFlags = data.readInt()
                val forwarded = Parcel.obtain()
                forwarded.appendFrom(data, data.dataPosition(), data.dataAvail())
                val identity = Binder.clearCallingIdentity()
                try {
                    target.transact(targetCode, forwarded, reply, targetFlags)
                } finally {
                    Binder.restoreCallingIdentity(identity)
                    forwarded.recycle()
                }
                return true
            }
            return super.onTransact(code, data, reply, flags)
        }

        private fun requireOwner() {
            if (Binder.getCallingUid() != ownerUid) {
                throw SecurityException("caller ${Binder.getCallingUid()} is not the owner")
            }
        }
    }

    private fun deliver(packageName: String, ownerUid: Int, binder: IBinder) {
        val userId = ownerUid / PerUserRange
        val authority = packageName + ShellProtocol.HandbackAuthoritySuffix
        val activityManager = Class.forName("android.os.ServiceManager")
            .getMethod("getService", String::class.java)
            .invoke(null, "activity") as IBinder
        val amInterface = Class.forName("android.app.IActivityManager\$Stub")
            .getMethod("asInterface", IBinder::class.java)
            .invoke(null, activityManager)
        val amClass = Class.forName("android.app.IActivityManager")
        val holder = amClass.getMethod(
            "getContentProviderExternal",
            String::class.java, Int::class.javaPrimitiveType, IBinder::class.java, String::class.java
        ).invoke(amInterface, authority, userId, null, authority)
        val provider = holder.javaClass.getField("provider").get(holder)
        try {
            val extras = Bundle().apply {
                putParcelable(ShellProtocol.HandbackExtraBinder, BinderContainer(binder))
            }
            val source = AttributionSource.Builder(ownerUid).setPackageName(packageName).build()
            Class.forName("android.content.IContentProvider").getMethod(
                "call",
                AttributionSource::class.java, String::class.java, String::class.java,
                String::class.java, Bundle::class.java
            ).invoke(provider, source, authority, ShellProtocol.HandbackMethod, null, extras)
        } finally {
            runCatching {
                amClass.getMethod("removeContentProviderExternal", String::class.java, IBinder::class.java)
                    .invoke(amInterface, authority, null)
            }
        }
    }

    private fun watchForUninstall(sourceDir: String) {
        val apk = File(sourceDir)
        Thread {
            while (apk.exists()) {
                Thread.sleep(UninstallCheckMillis)
            }
            System.exit(0)
        }.apply { isDaemon = true }.start()
    }

    private const val ServerVersion = 1
    private const val PerUserRange = 100000
    private const val UninstallCheckMillis = 10_000L
}
