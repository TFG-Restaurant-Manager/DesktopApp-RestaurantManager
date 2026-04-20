package com.tfg_rm.desktopapp_restaurantmanager.data.remote.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

object NetworkProvider {

    fun createHttpClient(tokenProvider: TokenProvider): HttpClient {
        return HttpClient(CIO) {

            expectSuccess = true

            install(ContentNegotiation) {
                json(
                    Json {
                        encodeDefaults = true
                        ignoreUnknownKeys = true
                    }
                )
            }

            install(WebSockets)

            defaultRequest {
                url(NetworkConfig.BASE_URL)

                tokenProvider.getToken()?.let {
                    header("Authorization", "Bearer $it")
                }
            }

            HttpResponseValidator {
                handleResponseExceptionWithRequest { exception, _ ->

                    val clientException = exception as? ClientRequestException
                    val status = clientException?.response?.status?.value

                    println("STATUS: $status")

                    if (status == 401 || status == 403) {
                        runBlocking {
                            tokenProvider.clearToken()
                            SessionManager.notifySessionExpired()
                        }
                    }
                }
            }
        }
    }
}