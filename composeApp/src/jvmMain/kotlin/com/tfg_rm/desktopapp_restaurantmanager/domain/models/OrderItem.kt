package com.tfg_rm.desktopapp_restaurantmanager.domain.models

data class OrderItem(
    val id: Int,
    val dish: Dishes,
    val quantity: Int,
    val notes: String? = null
)
