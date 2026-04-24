package com.tfg_rm.desktopapp_restaurantmanager.domain.models

/**
 * Links a full [Ingredient] to the quantity required by this dish.
 * No field duplication — all ingredient details live in [ingredient].
 */
data class DishIngredient(
    val ingredient: Ingredient,
    val quantity: Double
)

data class Dishes(
    val id: Int,
    val restaurantId: Int = 1,
    val name: String,
    val description: String?,
    val price: Double,
    val available: Boolean,
    val category: Category,
    val ingredients: List<DishIngredient> = emptyList()
)
