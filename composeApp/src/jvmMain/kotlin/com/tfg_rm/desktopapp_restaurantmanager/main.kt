package com.tfg_rm.desktopapp_restaurantmanager

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "DesktopAppRestaurantManager",
    ) {
        App()
    }
}