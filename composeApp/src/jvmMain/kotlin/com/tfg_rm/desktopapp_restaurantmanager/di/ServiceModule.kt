package com.tfg_rm.desktopapp_restaurantmanager.di

import com.tfg_rm.desktopapp_restaurantmanager.data.repository.AuthRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.DishesRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.EmployeesRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.IngredientsRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.OrdersRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.ScheduleRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.TablesRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.DishesService
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.EmployeesService
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.IngredientsService
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.LoginService
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.OrdersService
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.ScheduleService
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.TablesService
import org.koin.dsl.module

val serviceModule = module {
    single { LoginService(get()) }
    single { DishesService(get(), get()) }
    single { EmployeesService(get()) }
    single { IngredientsService(get()) }
    single { OrdersService(get(), get()) }
    single { ScheduleService(get()) }
    single { TablesService(get(), get()) }
}