package com.tfg_rm.desktopapp_restaurantmanager

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import com.tfg_rm.desktopapp_restaurantmanager.di.appModule

fun main() = application {
    // Iniciar Koin con el módulo de la app
    startKoin { modules(appModule) }

    val koin = GlobalContext.get()
    val viewModel = koin.get<com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.ExampleViewModel>()

    Window(
        onCloseRequest = ::exitApplication,
        title = "DesktopAppRestaurantManager",
    ) {
        App(viewModel)
    }
}