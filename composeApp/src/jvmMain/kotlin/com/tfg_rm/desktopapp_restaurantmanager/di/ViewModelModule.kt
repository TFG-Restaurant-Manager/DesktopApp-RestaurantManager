package com.tfg_rm.desktopapp_restaurantmanager.di

import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.*
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {

    viewModelOf(::LoginViewModel)
    viewModelOf(::DishesViewModel)
    viewModelOf(::EmployeesViewModel)
    viewModelOf(::InventoryViewModel)
    viewModelOf(::EconomyViewModel)
    viewModelOf(::OrdersViewModel)
    viewModelOf(::TablesViewModel)
    viewModelOf(::EconomyViewModel)
    viewModelOf(::NewOrderViewModel)
    viewModelOf(::OrderHistoryViewModel)
}