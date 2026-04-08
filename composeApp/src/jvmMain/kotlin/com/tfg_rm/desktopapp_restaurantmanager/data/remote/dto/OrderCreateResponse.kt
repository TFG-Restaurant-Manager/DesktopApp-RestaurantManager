package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderCreateResponse(
    val id: Long,
    val status: String,
    val total: Double,
    val createdAt: String
)
