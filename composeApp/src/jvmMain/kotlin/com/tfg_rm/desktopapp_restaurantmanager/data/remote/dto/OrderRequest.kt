package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    val id: Long? = null,
    val status: String,
    val type: String,           // "TABLE" | "DELIVERY" | "PICKUP"
    val tableId: Long? = null,
    val tableName: String?,
    val notes: String? = null,
    val createdAt: String,
    val deliveryAddress: String? = null,
    val deliveryNotes: String? = null,
    val pickupTime: String? = null,
    val items: List<OrderItemRequest>
)
