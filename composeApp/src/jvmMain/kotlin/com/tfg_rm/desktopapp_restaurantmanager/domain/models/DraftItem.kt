package com.tfg_rm.desktopapp_restaurantmanager.domain.models

data class DraftItem(
    val dish: Dishes,
    val quantity: Int = 1,
    val notes: String = "",
    /** Key = ingredient id, Value = "NORMAL" | "REMOVE" | "EXTRA" */
    val ingredientMods: Map<Int, String> = emptyMap()
)
