package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.AuthRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.network.TokenProvider

class AuthRepository(
    private val remote: AuthRemoteDataSource,
    private val tokenProvider: TokenProvider
) {
    suspend fun requestToken(code: String, password: String) {
        val response = remote.requestToken(code = code, password = password)
        tokenProvider.setToken(response.token)

        //tokenProvider.setToken("simulated-token-$code")
    }

    suspend fun logout() {
        tokenProvider.clearToken()
    }

    fun loadToken(): Boolean {
        return tokenProvider.loadToken()
    }
}
