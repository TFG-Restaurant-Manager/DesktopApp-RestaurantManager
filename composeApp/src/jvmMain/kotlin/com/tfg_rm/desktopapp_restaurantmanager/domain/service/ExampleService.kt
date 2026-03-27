package com.tfg_rm.desktopapp_restaurantmanager.domain.service

import com.tfg_rm.desktopapp_restaurantmanager.data.repository.ExampleRepository

class ExampleService(
    val repository: ExampleRepository = ExampleRepository()
) {
    suspend fun getGreeting(): String = repository.fetchGreeting()
}