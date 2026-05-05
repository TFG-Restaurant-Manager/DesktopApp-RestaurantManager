package com.tfg_rm.desktopapp_restaurantmanager.domain.models

import java.time.LocalDateTime

data class OrderHistorical(
    val orderId: Int,
    val type: String,
    val status: String,
    val total: Double,
    val notes: String?,
    val createdAt: LocalDateTime,
    val items: List<OrderItem>,
    val pickupTime: LocalDateTime?,
    val deliveryAddress: String?,
    val tableId: Int?
)