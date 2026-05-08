package com.tfg_rm.desktopapp_restaurantmanager.data.remote.network

import java.util.prefs.Preferences

class TokenProvider {

    private val prefs = Preferences.userRoot().node("com/tfg_rm/desktopapp_restaurantmanager")
    private val tokenKey = "token"

    private val roleKey = "role"

    private var token: String? = null

    private var role: String? = null

    fun loadToken(): Boolean {
        token = prefs.get(tokenKey, null)?.takeIf { it.isNotEmpty() }
        role = prefs.get(roleKey, null)?.takeIf { it.isNotEmpty() }
        return token != null && role != null
    }

    fun getToken(): String? = token

    fun getRole(): String? = role

    fun setToken(newToken: String, newRole: String) {
        token = newToken
        role = newRole
        prefs.put(tokenKey, newToken)
        prefs.put(roleKey, newRole)
    }

    fun clearToken() {
        token = null
        role = null
        prefs.remove(tokenKey)
        prefs.remove(roleKey)
    }
}
