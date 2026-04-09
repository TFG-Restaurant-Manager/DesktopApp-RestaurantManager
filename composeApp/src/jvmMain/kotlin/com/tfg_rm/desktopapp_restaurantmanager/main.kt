package com.tfg_rm.desktopapp_restaurantmanager

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.tfg_rm.desktopapp_restaurantmanager.di.dataSourceModule
import com.tfg_rm.desktopapp_restaurantmanager.di.networkModule
import com.tfg_rm.desktopapp_restaurantmanager.di.repositoryModule
import com.tfg_rm.desktopapp_restaurantmanager.di.serviceModule
import com.tfg_rm.desktopapp_restaurantmanager.di.viewModelModule
import org.koin.core.context.startKoin

fun main() = application {

    val windowState = rememberWindowState(placement = WindowPlacement.Maximized)
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "DesktopAppRestaurantManager",
    ) {
        App()
    }
}