package com.tfg_rm.desktopapp_restaurantmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tfg_rm.desktopapp_restaurantmanager.ui.screens.LoginScreen
import com.tfg_rm.desktopapp_restaurantmanager.ui.screens.MainScreen

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf(AppScreens.LoginScreen.route) }

    when (currentScreen) {
        AppScreens.LoginScreen.route -> {
            LoginScreen(
                navigate = { route -> currentScreen = route },
            )
        }
        AppScreens.MainScreen.route -> {
            MainScreen(
                navigate = { route -> currentScreen = route }
            )
        }
    }
}