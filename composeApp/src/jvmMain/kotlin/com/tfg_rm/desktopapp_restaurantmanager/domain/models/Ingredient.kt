package com.tfg_rm.desktopapp_restaurantmanager.domain.models

data class Ingredient(
    val id: Int,
    val restaurantId: Int,
    val name: String,
    val unit: String,
    val stockQuantity: Double,
    val costUnit: Double,
    val category: String,
    val minimumStock: Double,
    val usableInDishes: Boolean = true
)
