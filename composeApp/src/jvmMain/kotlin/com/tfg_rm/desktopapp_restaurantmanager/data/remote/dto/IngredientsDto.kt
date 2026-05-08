package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class IngredientsDto(
    val id: Int,
    val name: String,
    val unit: String,
    val stockQuantity: Double,
    val costUnit: Double,
    val minimumStock: Double,
    val category: String
)