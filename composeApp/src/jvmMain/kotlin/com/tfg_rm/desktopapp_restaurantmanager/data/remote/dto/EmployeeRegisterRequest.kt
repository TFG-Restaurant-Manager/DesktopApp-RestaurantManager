package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeRegisterRequest(
    val name: String,
    val roleName: String,       // MANAGER | WAITER | COOKER | ADMIN
    val active: Boolean = true,
    val email: String,
    val phone: String? = null,
    val startDate: String,      // "YYYY-MM-DD"
    val endDate: String? = null,
    val positionNotes: String? = null,
    val code: String,           // 10 dígitos — el trigger del backend prepone el prefijo
    val password: String
)
