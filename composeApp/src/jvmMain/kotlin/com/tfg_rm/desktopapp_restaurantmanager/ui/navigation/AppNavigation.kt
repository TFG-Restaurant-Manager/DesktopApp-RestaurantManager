package com.tfg_rm.desktopapp_restaurantmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
            is AuthState.Success -> currentScreen = AppScreens.MainScreen.route
            is AuthState.LogOut, AuthState.Idle -> {
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
                navigate = { route -> currentScreen = route }
            )
        }
    }
}