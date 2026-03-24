package com.tfg_rm.desktopapp_restaurantmanager.util

import org.koin.dsl.module
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.ExampleService
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.ExampleRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.OrdersRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.service.OrdersService
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.EconomyViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.EmployeesViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.ExampleViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.InventoryViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.LoginViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.OrderHistoryViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.OrdersViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.ScheduleViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.TablesViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf

val appModule = module {
    singleOf(::ExampleService)
    singleOf(::ExampleRepository)
    singleOf(::OrdersRepository)
    singleOf(::OrdersService)

    viewModelOf(::ExampleViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::OrdersViewModel)
    viewModelOf(::TablesViewModel)
    viewModelOf(::EmployeesViewModel)
    viewModelOf(::ScheduleViewModel)
    viewModelOf(::InventoryViewModel)
    viewModelOf(::EconomyViewModel)
    viewModelOf(::OrderHistoryViewModel)
}
