package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.AuthRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.network.SocketManager
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.network.TokenProvider

class AuthRepository(
    private val remote: AuthRemoteDataSource,
    private val tokenProvider: TokenProvider,
    private val socketManager: SocketManager
) {
    suspend fun requestToken(code: String, password: String) {
        val response = remote.requestToken(code = code, password = password)
        val token = response.token
        println("Token antes al meterlo en el token provider")
        tokenProvider.setToken(token)
    }

    suspend fun logout() {
        tokenProvider.clearToken()
        socketManager.disconnect()
    }

    fun loadToken(): Boolean {
        return tokenProvider.loadToken()
    }

    suspend fun connectWS() {
        socketManager.connect()
        socketManager.listen()
    }
}
