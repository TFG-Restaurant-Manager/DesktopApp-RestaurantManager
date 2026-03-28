package com.tfg_rm.desktopapp_restaurantmanager.util

import org.koin.dsl.module
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.ExampleService
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.ExampleRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.OrdersRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.OrdersService
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.EmployeesRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.EmployeesService
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.IngredientsRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.IngredientsService
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.DishesRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.DishesService
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.ScheduleRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.ScheduleService
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.TablesService
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.LoginService
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.OrderHistoryService
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.EconomyService
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.EconomyViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.EmployeesViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.ExampleViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.InventoryViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.DishesViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.LoginViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.OrderHistoryViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.OrdersViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.ScheduleViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.TablesViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf

val appModule = module {
    singleOf(::ExampleService)
    singleOf(::ExampleRepository)
    singleOf(::OrdersRepository)
    singleOf(::OrdersService)
    singleOf(::EmployeesRepository)
    singleOf(::EmployeesService)
    singleOf(::IngredientsRepository)
    singleOf(::IngredientsService)
    singleOf(::DishesRepository)
    singleOf(::DishesService)
    singleOf(::ScheduleRepository)
    singleOf(::ScheduleService)
    singleOf(::TablesService)
    singleOf(::LoginService)
    singleOf(::OrderHistoryService)
    singleOf(::EconomyService)

    viewModelOf(::ExampleViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::OrdersViewModel)
    viewModelOf(::TablesViewModel)
    viewModelOf(::EmployeesViewModel)
    viewModelOf(::ScheduleViewModel)
    viewModelOf(::InventoryViewModel)
    viewModelOf(::DishesViewModel)
    viewModelOf(::EconomyViewModel)
    viewModelOf(::OrderHistoryViewModel)
}
