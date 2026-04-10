package com.tfg_rm.desktopapp_restaurantmanager.di

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataSourceModule = module {
    singleOf(::AuthRemoteDataSource)
    singleOf(::DishesRemoteDataSource)
    singleOf(::EmployeesRemoteDataSource)
    singleOf(::OrdersRemoteDataSource)
    singleOf(::ScheduleRemoteDataSource)
    singleOf(::TablesRemoteDataSource)
    singleOf(::TablesOrdersDataSource)
    singleOf(::OrdersRemoteDataSource)
    singleOf(::InventoryRemoteDataSource)
}