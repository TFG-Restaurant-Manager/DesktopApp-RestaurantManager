package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderCreateRequest(
    val type: String,           // "TABLE" | "DELIVERY" | "PICKUP"
    val tableId: Long? = null,
    val notes: String? = null,
    val items: List<OrderItemRequest>
)
