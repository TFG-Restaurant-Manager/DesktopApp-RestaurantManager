package com.tfg_rm.desktopapp_restaurantmanager.util

import org.koin.dsl.module
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.ExampleService
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.ExampleRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.ExampleViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf

val appModule = module {
    singleOf(::ExampleService)
    singleOf(::ExampleRepository)
    viewModelOf(::ExampleViewModel)

}
