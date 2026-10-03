// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.domain.model

data class ShellResult(
    val code: Int,
    val out: String,
    val err: String
) {
    val ok: Boolean get() = code == 0

    companion object {
        val Unavailable = ShellResult(code = -1, out = "", err = "shell access not ready")
    }
}
