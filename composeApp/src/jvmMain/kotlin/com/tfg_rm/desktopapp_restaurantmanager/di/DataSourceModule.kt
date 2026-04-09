package com.tfg_rm.desktopapp_restaurantmanager.di

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.AuthRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.DishesRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.EmployeesRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.OrdersRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.ScheduleRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.TablesOrdersDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.TablesRemoteDataSource
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
}