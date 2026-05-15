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

    // Estado para saber si debemos intentar reconectar
    private var isActive = false

    private val _messages = MutableSharedFlow<String>()
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    suspend fun connect() {
        try {
            disconnect()
            isActive = true
            session = client.webSocketSession {
                url("${NetworkConfig.WS_URL}api/ws")
            }
            println("Conexión establecida correctamente")
        } catch (e: Exception) {
            println("Error al conectar: ${e.message}")
            handleReconnection()
        }
    }

    suspend fun sendMessage(message: String) {
        try {
            session?.send(Frame.Text(message))
        } catch (e: Exception) {
            println("No se pudo enviar el mensaje: ${e.message}")
            e.printStackTrace()
        }
    }

    suspend fun listen() {
        while (isActive) { // Bucle infinito mientras queramos estar conectados
            try {
                session?.let { socketSession ->
                    for (frame in socketSession.incoming) {
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            _messages.emit(text)
                        }
                    }
                }
                // Si el bucle termina sin excepción, es que el servidor cerró normal
                println("El servidor cerró la conexión.")
            } catch (e: Exception) {
                // Aquí cae el EOFException, ClosedReceiveChannelException, etc.
                println("Error en la escucha: ${e.message}")
            }

            // Si llegamos aquí, la conexión se ha perdido
            if (isActive) {
                handleReconnection()
            }
        }
    }

    private suspend fun handleReconnection() {
        println("Intentando reconectar en 5 segundos...")
        session = null
        kotlinx.coroutines.delay(5000) // Espera antes de reintentar para no saturar
        connect()
    }

    suspend fun disconnect() {
        isActive = false
        try {
            session?.close()
        } catch (e: Exception) {
            println("Error al cerrar sesión: ${e.message}")
            e.printStackTrace()
        } finally {
            session = null
        }
    }
}