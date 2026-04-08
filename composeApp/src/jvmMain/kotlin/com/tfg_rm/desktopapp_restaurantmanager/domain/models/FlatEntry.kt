package com.tfg_rm.desktopapp_restaurantmanager.domain.models

data class FlatEntry(
    val displayNumber: Int,
    val order: Order,
    val item: OrderItem
)
