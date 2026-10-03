// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.infrastructure.adb

import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.math.BigInteger
import java.security.KeyPair
import java.security.cert.X509Certificate
import java.util.Date
import javax.security.auth.x500.X500Principal

object SelfSignedCertificate {
    private const val SignatureAlgorithm = "SHA512withRSA"
    private const val OneYearMillis = 365L * 24 * 60 * 60 * 1000
    private const val ValidYears = 30

    fun build(pair: KeyPair, subject: X500Principal): X509Certificate {
        val now = System.currentTimeMillis()
        val builder = JcaX509v3CertificateBuilder(
            subject,
            BigInteger.valueOf(now),
            Date(now - OneYearMillis),
            Date(now + ValidYears * OneYearMillis),
            subject,
            pair.public
        )
        val signer = JcaContentSignerBuilder(SignatureAlgorithm).build(pair.private)
        return JcaX509CertificateConverter().getCertificate(builder.build(signer))
    }
}
