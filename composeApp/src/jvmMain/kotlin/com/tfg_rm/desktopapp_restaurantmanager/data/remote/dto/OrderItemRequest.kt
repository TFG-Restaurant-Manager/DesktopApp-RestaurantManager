package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderItemRequest(
    val dishId: Long,
    val notes: String? = null,
    val unitPrice: Double
)
