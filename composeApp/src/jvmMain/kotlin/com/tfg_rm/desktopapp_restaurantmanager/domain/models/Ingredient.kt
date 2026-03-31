package com.tfg_rm.desktopapp_restaurantmanager.domain.models

data class Ingredient(
    // ── Server fields (DB: ingredients) ─────────────────────────────────
    val id: Int,
    val restaurantId: Int,
    val name: String,
    val unit: String,
    val stockQuantity: Double,
    val costUnit: Double,
    // ── Local-only fields (not in DB — suggested additions to server) ────
    val category: String,        // suggest: ingredients.category_id FK
    val minimumStock: Double,    // suggest: ingredients.minimum_stock
    val usableInDishes: Boolean = true  // desktop-only, never sync
)
