package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderCreatedResponse(
    val type: String = "CREATED_ORDER",
    val payload: OrderResponse
)
