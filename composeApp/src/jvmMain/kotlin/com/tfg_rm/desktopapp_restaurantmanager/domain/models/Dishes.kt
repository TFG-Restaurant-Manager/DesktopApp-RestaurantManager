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
    // ── Server fields (DB: dishes) ───────────────────────────────────────
    val id: Int,
    val restaurantId: Int = 1,
    val categoryId: Int? = null,
    val name: String,
    val description: String?,
    val price: Double,
    val available: Boolean,
    // ── Display helper (resolved from categories table on server) ────────
    val categoryName: String? = "",
    // ── Resolved relation (from dish_ingredients join) ───────────────────
    val ingredients: List<DishIngredient> = emptyList()
)
