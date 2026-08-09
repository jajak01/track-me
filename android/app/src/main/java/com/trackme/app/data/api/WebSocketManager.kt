package com.trackme.app.data.api

import com.trackme.app.BuildConfig
import com.trackme.app.data.local.TokenManager
import com.trackme.app.data.model.LocationResponse
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import okhttp3.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

sealed class WsEvent {
    data class LocationUpdate(val data: LocationResponse) : WsEvent()
    data class Notification(val type: String, val title: String, val body: String) : WsEvent()
    data class SharingUpdate(val friendId: String, val status: String) : WsEvent()
    data class FriendUpdate(val data: JsonObject) : WsEvent()
    object Heartbeat : WsEvent()
    data class Unknown(val raw: String) : WsEvent()
}

@Singleton
class WebSocketManager @Inject constructor(
    private val tokenManager: TokenManager
) {
    private var ws: WebSocket? = null
    private val json = Json { ignoreUnknownKeys = true }
    private val client = OkHttpClient.Builder()
        .pingInterval(54, TimeUnit.SECONDS)
        .build()

    fun connect(onEvent: (WsEvent) -> Unit, onDisconnect: (String) -> Unit) {
        val token = runBlocking { tokenManager.getAccessToken() } ?: run {
            onDisconnect("No token")
            return
        }

        val url = "${BuildConfig.WS_URL}?token=$token"
        val request = Request.Builder().url(url).build()

        ws = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {}

            override fun onMessage(webSocket: WebSocket, text: String) {
                val event = parseEvent(text)
                when (event) {
                    is WsEvent.Heartbeat -> { /* handled by OkHttp ping/pong */ }
                    else -> onEvent(event)
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                onDisconnect(reason)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                onDisconnect(t.message ?: "WebSocket error")
            }
        })
    }

    fun disconnect() {
        ws?.close(1000, "User disconnected")
        ws = null
    }

    private fun parseEvent(text: String): WsEvent {
        return try {
            val raw = json.decodeFromString<JsonObject>(text)
            when (raw["event"]?.toString()?.trim('"')) {
                "heartbeat" -> WsEvent.Heartbeat
                "location:update" -> {
                    val loc = json.decodeFromString<LocationResponse>(raw["data"].toString())
                    WsEvent.LocationUpdate(loc)
                }
                "notification" -> {
                    val data = raw["data"] as? JsonObject
                    WsEvent.Notification(
                        type = data?.get("type")?.toString()?.trim('"') ?: "",
                        title = data?.get("title")?.toString()?.trim('"') ?: "",
                        body = data?.get("body")?.toString()?.trim('"') ?: ""
                    )
                }
                "sharing:update" -> {
                    val data = raw["data"] as? JsonObject
                    WsEvent.SharingUpdate(
                        friendId = data?.get("friend_id")?.toString()?.trim('"') ?: "",
                        status = data?.get("status")?.toString()?.trim('"') ?: ""
                    )
                }
                "friend:update" -> WsEvent.FriendUpdate(raw["data"] as? JsonObject ?: JsonObject(emptyMap()))
                else -> WsEvent.Unknown(text)
            }
        } catch (e: Exception) {
            WsEvent.Unknown(text)
        }
    }
}
