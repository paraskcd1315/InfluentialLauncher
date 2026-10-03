// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.infrastructure.adb

import io.github.muntashirakon.adb.AbsAdbConnectionManager
import java.security.PrivateKey
import java.security.cert.Certificate

class AdbConnectionManager(
    private val keys: AdbKeyStore,
    api: Int
) : AbsAdbConnectionManager() {
    init {
        setApi(api)
    }

    override fun getPrivateKey(): PrivateKey = keys.privateKey()

    override fun getCertificate(): Certificate = keys.certificate()

    override fun getDeviceName(): String = DeviceName

    private companion object {
        const val DeviceName = "Influential Launcher"
    }
}
