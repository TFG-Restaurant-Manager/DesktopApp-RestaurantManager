package com.tfg_rm.desktopapp_restaurantmanager.domain.models

data class Employee(
    val id: Int,
    val roleName: String,
    val name: String,
    val email: String,
    val phone: String,
    val code: String = "",
    val startDate: String = "",
    val active: Boolean = true
)
