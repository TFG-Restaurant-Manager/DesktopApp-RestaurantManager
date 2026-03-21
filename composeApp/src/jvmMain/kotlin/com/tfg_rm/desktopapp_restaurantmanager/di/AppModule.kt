package com.tfg_rm.desktopapp_restaurantmanager.di

import org.koin.dsl.module
import com.tfg_rm.desktopapp_restaurantmanager.data.service.ExampleService
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.ExampleRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.ExampleViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.LoginViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.OrdersViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.TablesViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.EmployeesViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.ScheduleViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.InventoryViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.EconomyViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.OrderHistoryViewModel

val appModule = module {
    single { ExampleService() }
    single { ExampleRepository(get()) }
    single { ExampleViewModel(get()) }
    single { LoginViewModel() }
    single { OrdersViewModel() }
    single { TablesViewModel() }
    single { EmployeesViewModel() }
    single { ScheduleViewModel() }
    single { InventoryViewModel() }
    single { EconomyViewModel() }
    single { OrderHistoryViewModel() }
}
