package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    val id: Long? = null,
    val type: String,           // "TABLE" | "DELIVERY" | "PICKUP"
    val tableId: Long? = null,
    val notes: String? = null,
    val createdAt: String,
    val deliveryAddress: String? = null,
    val deliveryNotes: String? = null,
    val items: List<OrderItemRequest>
)
