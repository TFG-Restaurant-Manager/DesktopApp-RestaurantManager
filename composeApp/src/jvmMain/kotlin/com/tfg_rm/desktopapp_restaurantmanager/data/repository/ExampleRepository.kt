package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.data.service.ExampleService

class ExampleRepository(private val service: ExampleService) {
    suspend fun getGreeting(): String = service.fetchGreeting()
}
