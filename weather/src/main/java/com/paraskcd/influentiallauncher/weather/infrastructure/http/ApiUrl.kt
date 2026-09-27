package com.paraskcd.influentiallauncher.weather.infrastructure.http

import java.net.URLEncoder

object ApiUrl {
    fun of(base: String, path: String, query: Map<String, Any?> = emptyMap()): String {
        val params = query.entries
            .filter { it.value != null }
            .joinToString("&") { (name, value) -> "${encode(name)}=${encode(value.toString())}" }
        return if (params.isEmpty()) base + path else "$base$path?$params"
    }

    private fun encode(value: String): String = URLEncoder.encode(value, Charsets.UTF_8.name())
}
