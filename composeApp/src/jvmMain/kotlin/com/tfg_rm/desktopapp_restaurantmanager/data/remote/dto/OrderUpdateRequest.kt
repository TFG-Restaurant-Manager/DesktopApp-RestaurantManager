package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderUpdateRequest(
    val type: String = "UPDATE_ORDER",
    val payload: OrderRequest
)