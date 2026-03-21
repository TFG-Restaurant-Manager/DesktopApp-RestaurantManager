package com.tfg_rm.desktopapp_restaurantmanager.data.service

import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.delay

class ExampleService {
    suspend fun fetchGreeting(): String {
        delay(400)
        return Strings.t("service.hello")
    }
}
