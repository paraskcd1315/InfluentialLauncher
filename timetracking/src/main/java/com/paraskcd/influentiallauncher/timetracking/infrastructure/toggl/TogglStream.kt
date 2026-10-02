// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.infrastructure.toggl

import android.util.Log
import com.paraskcd.influentiallauncher.timetracking.domain.model.StreamSignal
import com.paraskcd.influentiallauncher.timetracking.domain.model.Tracker
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerCredentials
import com.paraskcd.influentiallauncher.timetracking.domain.ports.TrackerStream
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TogglStream @Inject constructor() : TrackerStream {

    override val tracker: Tracker = Tracker.Toggl

    private val client by lazy {
        OkHttpClient.Builder().pingInterval(PingSeconds, TimeUnit.SECONDS).build()
    }

    override fun signals(credentials: TrackerCredentials): Flow<StreamSignal> = flow {
        var attempt = 0
        while (true) {
            connection(credentials.togglToken.trim())
                .catch { Log.w(LogTag, "stream failed: ${it.javaClass.simpleName}") }
                .collect { signal ->
                    if (signal == StreamSignal.Open) attempt = 0
                    emit(signal)
                }
            emit(StreamSignal.Closed)
            delay(backoffMs(attempt))
            attempt += 1
        }
    }

    private fun connection(token: String): Flow<StreamSignal> = callbackFlow {
        val request = Request.Builder()
            .url(TogglApi.Stream.Url)
            .header(TogglApi.Stream.OriginHeader, TogglApi.Stream.Origin)
            .build()
        val listener = object : WebSocketListener() {
            private var answered = false

            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send(
                    JSONObject()
                        .put(TogglApi.Stream.TypeField, TogglApi.Stream.Authenticate)
                        .put(TogglApi.Stream.TokenField, token)
                        .toString()
                )
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                if (!answered) {
                    answered = true
                    Log.i(LogTag, "stream open")
                    trySend(StreamSignal.Open)
                }
                val message = runCatching { JSONObject(text) }.getOrNull() ?: return
                when {
                    message.optString(TogglApi.Stream.TypeField) == TogglApi.Stream.Ping ->
                        webSocket.send(JSONObject().put(TogglApi.Stream.TypeField, TogglApi.Stream.Pong).toString())
                    message.optString(TogglApi.Stream.ModelField) == TogglApi.Stream.TimeEntryModel -> {
                        Log.i(LogTag, "time entry changed")
                        trySend(StreamSignal.Changed)
                    }
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(code, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                close()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                close(t)
            }
        }
        val socket = client.newWebSocket(request, listener)
        awaitClose { socket.cancel() }
    }

    private fun backoffMs(attempt: Int): Long =
        (BackoffStartMs shl attempt.coerceAtMost(BackoffMaxShift)).coerceAtMost(BackoffCeilingMs)

    private companion object {
        const val LogTag = "TogglStream"
        const val PingSeconds = 30L
        const val BackoffStartMs = 2_000L
        const val BackoffCeilingMs = 60_000L
        const val BackoffMaxShift = 5
    }
}
