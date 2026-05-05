package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderResponse(
    val orderId: Int,
    val type: String,
    val status: String,
    val total: Double,
    val notes: String?,
    val createdAt: String,
    val items: List<OrderItemResponse>,
    val pickupTime: String?,
    val deliveryAddress: String?,
    val tableId: Int?
)