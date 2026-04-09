package com.tfg_rm.desktopapp_restaurantmanager.data.remote.network

class TokenProvider {

    private var token: String? = null

    /** Always returns false on startup — token is in-memory only, not persisted. */
    fun loadToken(): Boolean = token != null

    fun getToken(): String? = token

    fun setToken(newToken: String) {
        token = newToken
    }

    fun clearToken() {
        token = null
    }
}
