package com.tfg_rm.desktopapp_restaurantmanager.di

import com.tfg_rm.desktopapp_restaurantmanager.data.repository.AuthRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.DishesRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.EmployeesRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.IngredientsRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.OrdersRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.ScheduleRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.TablesOrdersRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.TablesRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::AuthRepository)
    singleOf(::DishesRepository)
    singleOf(::EmployeesRepository)
    singleOf(::IngredientsRepository)
    singleOf(::OrdersRepository)
    singleOf(::ScheduleRepository)
    singleOf(::TablesRepository)
    singleOf(::TablesOrdersRepository)
    singleOf(::OrdersRepository)
}