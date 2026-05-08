package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DishIngredientRequest(
    val ingredientId: Int,
    val quantity: Double
)

@Serializable
data class DishCreateRequest(
    val name: String,
    val description: String,
    val categoryName: String,
    val price: Double,
    val available: Boolean,
    val restaurantId: Int,
    val ingredients: List<DishIngredientRequest>
)
