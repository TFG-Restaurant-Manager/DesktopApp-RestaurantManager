package com.tfg_rm.desktopapp_restaurantmanager

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.tfg_rm.desktopapp_restaurantmanager.di.dataSourceModule
import com.tfg_rm.desktopapp_restaurantmanager.di.networkModule
import com.tfg_rm.desktopapp_restaurantmanager.di.repositoryModule
import com.tfg_rm.desktopapp_restaurantmanager.di.serviceModule
import com.tfg_rm.desktopapp_restaurantmanager.di.viewModelModule
import com.tfg_rm.desktopapp_restaurantmanager.ui.navigation.AppNavigation
import com.tfg_rm.desktopapp_restaurantmanager.ui.theme.DesktopAppRestaurantManagerTheme
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration


@Composable
fun App() {
    KoinApplication(configuration = koinConfiguration {
        modules(
            networkModule,
            dataSourceModule,
            repositoryModule,
            serviceModule,
            viewModelModule
        )
    }) {
        DesktopAppRestaurantManagerTheme() {
            AppNavigation()
        }
    }
}

@Composable
@Preview
fun AppPreview() {
    App()
}