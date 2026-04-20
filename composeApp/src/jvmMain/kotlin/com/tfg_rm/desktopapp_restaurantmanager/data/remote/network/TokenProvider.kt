package com.tfg_rm.desktopapp_restaurantmanager.data.remote.network

import java.util.prefs.Preferences

class TokenProvider {

    private val prefs = Preferences.userRoot().node("com/tfg_rm/desktopapp_restaurantmanager")
    private val tokenKey = "token"

    private var token: String? = null

    fun loadToken(): Boolean {
        token = prefs.get(tokenKey, null)?.takeIf { it.isNotEmpty() }
        return token != null
    }

    fun getToken(): String? = token

    fun setToken(newToken: String) {
        token = newToken
        prefs.put(tokenKey, newToken)
    }

    fun clearToken() {
        token = null
        prefs.remove(tokenKey)
    }
}
