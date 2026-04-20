package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TableResponse(
    val tableId: Long,
    val tableName: String,
    val capacity: Int,
    val posX: Int,
    val posY: Int,
    val status: String,
    val sectionTitle: String? = null,
    val sectionId: Int,
    val orderId: Long? = null,
    val orderStatus: String? = null,
    val orderTotal: Double? = null,
    val orderNotes: String? = null,
    val orderCreatedAt: String? = null,
    val orderItems: List<OrderItemResponse> = emptyList()
)
