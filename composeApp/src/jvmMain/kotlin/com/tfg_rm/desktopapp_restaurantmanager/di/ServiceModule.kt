package com.tfg_rm.desktopapp_restaurantmanager.di

import com.tfg_rm.desktopapp_restaurantmanager.domain.service.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val serviceModule = module {
    singleOf(::LoginService)
    singleOf(::DishesService)
    singleOf(::EmployeesService)
    singleOf(::IngredientsService)
    singleOf(::OrdersService)
    singleOf(::TablesService)
    singleOf(::EconomyService)
    singleOf(::OrderHistoryService)
}