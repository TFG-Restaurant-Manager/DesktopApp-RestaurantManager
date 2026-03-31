package com.tfg_rm.desktopapp_restaurantmanager.domain.models

data class OrderItem(
    // ── Server fields (DB: order_items) ─────────────────────────────────
    val id: Int,
    val dish: Dishes,
    val unitPrice: Double,
    val notes: String? = null,
    // ── Local-only (not in DB — suggested: order_items.quantity) ─────────
    val quantity: Int = 1
)
