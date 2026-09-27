package com.paraskcd.influentiallauncher.timetracking.infrastructure.http

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JsonHttp @Inject constructor() {

    suspend fun request(
        method: String,
        url: String,
        authorization: String,
        body: String? = null
    ): String = withContext(Dispatchers.IO) {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = TimeoutMs
            readTimeout = TimeoutMs
            setRequestProperty("Authorization", authorization)
            setRequestProperty("Accept", "application/json")
            if (body != null) {
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
            }
        }
        try {
            if (body != null) connection.outputStream.use { it.write(body.toByteArray()) }
            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val text = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (code !in 200..299) throw TrackerHttpException(code, text.take(ErrorPreview))
            text
        } finally {
            connection.disconnect()
        }
    }

    private companion object {
        const val TimeoutMs = 15_000
        const val ErrorPreview = 200
    }
}

class TrackerHttpException(val code: Int, detail: String) : IOException("HTTP $code: $detail")
