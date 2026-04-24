package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DishesResponse(
    val id: Long,
    val name: String,
    val description: String? = null,
    val categoryId: Int,
    val categoryName: String,
    val price: Double,
    val available: Boolean,
    val ingredients: List<DishIngredientRequest>
)
