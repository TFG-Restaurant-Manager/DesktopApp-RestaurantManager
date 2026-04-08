package com.tfg_rm.desktopapp_restaurantmanager.di

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.AuthRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.DishesRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.EmployeesRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.OrdersRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.ScheduleRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.TablesOrdersDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.TablesRemoteDataSource
import org.koin.dsl.module

val dataSourceModule = module {
    single { AuthRemoteDataSource(get()) }
    single { DishesRemoteDataSource(get()) }
    single { EmployeesRemoteDataSource(get()) }
    single { OrdersRemoteDataSource(get()) }
    single { ScheduleRemoteDataSource(get()) }
    single { TablesRemoteDataSource(get()) }
    single { TablesOrdersDataSource(get()) }
}