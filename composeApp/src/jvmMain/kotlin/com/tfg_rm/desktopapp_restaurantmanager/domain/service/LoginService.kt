package com.tfg_rm.desktopapp_restaurantmanager.domain.service

import com.tfg_rm.desktopapp_restaurantmanager.data.repository.AuthRepository

class LoginService(
    private val authRepository: AuthRepository
) {
    suspend fun requestToken(code: String, password: String) =
        authRepository.requestToken(code = code, password = password)

    suspend fun logout() =
        authRepository.logout()

    fun loadToken(): Boolean =
        authRepository.loadToken()

    suspend fun connectBS() = authRepository.connectWS()
}
