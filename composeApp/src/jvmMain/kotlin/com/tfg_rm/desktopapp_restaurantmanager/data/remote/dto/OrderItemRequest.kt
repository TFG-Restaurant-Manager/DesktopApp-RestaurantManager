package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderItemRequest(
    val id: Long,
    val dishId: Long,
    val notes: String? = null,
    val status: String
)
