package com.tfg_rm.desktopapp_restaurantmanager.data.remote.network

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SocketManager(
    private val client: HttpClient
) {
    private var session: DefaultClientWebSocketSession? = null

    private val _messages = MutableSharedFlow<String>()
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    suspend fun connect() {
        disconnect()
        session = client.webSocketSession {
            url("${NetworkConfig.WS_URL}api/ws")
        }
        println("SocketManager conexion realizada")
    }

    suspend fun sendMessage(message: String) {
        session?.send(Frame.Text(message))
    }

    suspend fun listen() {
        session?.let { socketSession ->
            for (frame in socketSession.incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        println("Recibido: $text")
                        _messages.emit(text)
                    }

                    else -> {}
                }
            }
        }
    }

    suspend fun disconnect() {
        session?.let {
            it.close()
            session = null
            println("SocketManager websockets desconectados")
        }
    }
}