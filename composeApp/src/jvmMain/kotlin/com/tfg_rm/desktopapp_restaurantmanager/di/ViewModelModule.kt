package com.tfg_rm.desktopapp_restaurantmanager.di

import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.DishesViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.EconomyViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.EmployeesViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.InventoryViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.LoginViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.OrdersViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.ScheduleViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.TablesViewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel

val viewModelModule = module {

    viewModel { LoginViewModel(get()) }
    viewModel { DishesViewModel(get()) }
    viewModel { EmployeesViewModel(get()) }
    viewModel { InventoryViewModel(get()) }
    viewModel { EconomyViewModel(get()) }
    viewModel { OrdersViewModel(get()) }
    viewModel { TablesViewModel(get()) }
    viewModel { ScheduleViewModel(get(), get()) }
}