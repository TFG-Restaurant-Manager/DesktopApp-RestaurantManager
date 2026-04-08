package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeLoginRequest(
    val code: String,
    val password: String
)
