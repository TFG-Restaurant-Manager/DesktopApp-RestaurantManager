package com.tfg_rm.desktopapp_restaurantmanager.di

import com.tfg_rm.desktopapp_restaurantmanager.data.repository.AuthRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.DishesRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.EmployeesRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.IngredientsRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.OrdersRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.ScheduleRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.TablesOrdersRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.TablesRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { AuthRepository(get(), get()) }
    single { DishesRepository(get()) }
    single { EmployeesRepository(get()) }
    single { IngredientsRepository() }
    single { OrdersRepository(get()) }
    single { ScheduleRepository(get()) }
    single { TablesRepository(get()) }
    single { TablesOrdersRepository(get()) }
}