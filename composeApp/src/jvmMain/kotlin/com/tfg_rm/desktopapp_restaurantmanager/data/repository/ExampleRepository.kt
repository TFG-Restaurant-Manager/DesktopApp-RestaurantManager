package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.domain.service.ExampleService
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.delay

class ExampleRepository() {
    suspend fun fetchGreeting(): String {
        delay(400)
        return Strings.t("service.hello")
    }
}
