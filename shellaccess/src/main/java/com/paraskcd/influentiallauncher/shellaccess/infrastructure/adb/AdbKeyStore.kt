// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.infrastructure.adb

import android.content.Context
import android.util.Base64
import java.io.File
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.spec.PKCS8EncodedKeySpec
import javax.security.auth.x500.X500Principal

class AdbKeyStore(private val context: Context) {
    private val dir: File by lazy { File(context.filesDir, DirName).apply { mkdirs() } }
    private val keyFile: File get() = File(dir, KeyFileName)
    private val certFile: File get() = File(dir, CertFileName)

    val paired: Boolean get() = keyFile.exists() && certFile.exists()

    fun privateKey(): PrivateKey {
        ensure()
        val bytes = Base64.decode(keyFile.readText(), Base64.DEFAULT)
        return KeyFactory.getInstance(KeyAlgorithm).generatePrivate(PKCS8EncodedKeySpec(bytes))
    }

    fun certificate(): Certificate {
        ensure()
        certFile.inputStream().use { return CertificateFactory.getInstance(CertType).generateCertificate(it) }
    }

    fun clear() {
        keyFile.delete()
        certFile.delete()
    }

    private fun ensure() {
        if (paired) return
        val generator = KeyPairGenerator.getInstance(KeyAlgorithm)
        generator.initialize(KeySize, SecureRandom())
        val pair = generator.generateKeyPair()
        val certificate = SelfSignedCertificate.build(pair, X500Principal(Subject))
        keyFile.writeText(Base64.encodeToString(pair.private.encoded, Base64.DEFAULT))
        certFile.writeText(pemOf(certificate))
    }

    private fun pemOf(certificate: Certificate): String {
        val body = Base64.encodeToString(certificate.encoded, Base64.NO_WRAP)
        return buildString {
            append("-----BEGIN CERTIFICATE-----\n")
            body.chunked(PemLineWidth).forEach { append(it).append('\n') }
            append("-----END CERTIFICATE-----\n")
        }
    }

    private companion object {
        const val DirName = "shellaccess"
        const val KeyFileName = "adbkey.pk8"
        const val CertFileName = "adbkey.cert"
        const val KeyAlgorithm = "RSA"
        const val CertType = "X.509"
        const val KeySize = 2048
        const val PemLineWidth = 64
        const val Subject = "CN=Influential Launcher,O=Influential,C=US"
    }
}
