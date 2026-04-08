package com.tfg_rm.desktopapp_restaurantmanager.di

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.network.NetworkProvider
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.network.TokenProvider
import org.koin.dsl.module

val networkModule = module {

    single { TokenProvider() }

    single {
        NetworkProvider.createHttpClient(get())
    }
}