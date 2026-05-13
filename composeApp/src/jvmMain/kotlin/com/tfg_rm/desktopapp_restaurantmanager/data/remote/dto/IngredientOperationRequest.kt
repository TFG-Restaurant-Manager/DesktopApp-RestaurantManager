package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class IngredientOperationRequest(
    val name: String,
    val unit: String,
    val stockQuantity: Double,
    val costUnit: Double,
    val minimumStock: Double,
    val categoryId: Int,
    val categoryName: String
)