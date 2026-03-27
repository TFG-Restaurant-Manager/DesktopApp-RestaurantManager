package com.tfg_rm.desktopapp_restaurantmanager.ui.navigation

sealed class AppScreens(val route: String) {
    object MainScreen : AppScreens("main_screen")
    object LoginScreen : AppScreens("login_screen")
}