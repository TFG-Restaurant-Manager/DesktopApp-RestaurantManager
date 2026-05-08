package com.tfg_rm.desktopapp_restaurantmanager.ui.navigation

import androidx.compose.runtime.*
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.AuthState
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.LoginViewModel
import com.tfg_rm.desktopapp_restaurantmanager.ui.screens.LoginScreen
import com.tfg_rm.desktopapp_restaurantmanager.ui.screens.MainScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavigation() {
    val loginViewModel: LoginViewModel = koinViewModel()
    val authState by loginViewModel.authState.collectAsState()

    var currentScreen by remember { mutableStateOf(AppScreens.LoginScreen.route) }

    LaunchedEffect(Unit) {
        loginViewModel.login() // check for saved token on startup
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                currentScreen = AppScreens.MainScreen.route
            }

            is AuthState.LogOut -> {
                currentScreen = AppScreens.LoginScreen.route
                loginViewModel.resetState()
            }

            else -> {}
        }
    }

    when (currentScreen) {
        AppScreens.LoginScreen.route -> {
            LoginScreen(viewModel = loginViewModel)
        }

        AppScreens.MainScreen.route -> {
            MainScreen(
                { loginViewModel.logout() }
            )
        }
    }
}