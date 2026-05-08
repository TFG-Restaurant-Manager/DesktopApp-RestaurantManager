package com.tfg_rm.desktopapp_restaurantmanager.data.remote.network

import java.util.prefs.Preferences

class TokenProvider {

    private val prefs = Preferences.userRoot().node("com/tfg_rm/desktopapp_restaurantmanager")
    private val TOKEN_KEY = "token"

    private var token: String? = null

    fun loadToken(): Boolean {
        token = prefs.get(TOKEN_KEY, null)?.takeIf { it.isNotEmpty() }
        return token != null
    }

    fun getToken(): String? = token

    fun setToken(newToken: String) {
        token = newToken
        prefs.put(TOKEN_KEY, newToken)
    }

    fun clearToken() {
        token = null
        prefs.remove(TOKEN_KEY)
    }
}
