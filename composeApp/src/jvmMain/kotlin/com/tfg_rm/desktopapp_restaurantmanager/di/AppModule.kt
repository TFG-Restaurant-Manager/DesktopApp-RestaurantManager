package com.tfg_rm.desktopapp_restaurantmanager.di

import org.koin.dsl.module
import com.tfg_rm.desktopapp_restaurantmanager.data.service.ExampleService
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.ExampleRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.ExampleViewModel

val appModule = module {
    single { ExampleService() }
    single { ExampleRepository(get()) }
    single { ExampleViewModel(get()) }
}
