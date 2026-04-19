package com.tfg_rm.desktopapp_restaurantmanager.util

import java.util.*

object Strings {
    private fun bundle(): ResourceBundle = ResourceBundle.getBundle("strings", Locale.ENGLISH)

    fun t(key: String): String = try {
        bundle().getString(key)
    } catch (e: Exception) {
        println("LLave error strings: $key")
        throw e
    }
}
