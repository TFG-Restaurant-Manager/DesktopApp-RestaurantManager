package com.tfg_rm.desktopapp_restaurantmanager.util

import java.util.Locale
import java.util.ResourceBundle

object Strings {
    private fun bundle(): ResourceBundle = ResourceBundle.getBundle("strings", Locale.getDefault())

    fun t(key: String): String = try {
        bundle().getString(key)
    } catch (e: Exception) {
        // fallback to key if missing
        key
    }
}
