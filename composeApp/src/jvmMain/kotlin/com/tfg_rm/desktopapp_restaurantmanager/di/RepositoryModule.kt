package com.tfg_rm.desktopapp_restaurantmanager.di

import com.tfg_rm.desktopapp_restaurantmanager.data.repository.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::AuthRepository)
    singleOf(::DishesRepository)
    singleOf(::EmployeesRepository)
    singleOf(::IngredientsRepository)
    singleOf(::OrdersRepository)
    singleOf(::TablesRepository)
    singleOf(::TablesOrdersRepository)
}