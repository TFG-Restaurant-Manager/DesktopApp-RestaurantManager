package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Dishes

class DishesRepository {
    private var nextId = 6

    private val dishes = mutableListOf(
        Dishes(1, "Ensalada César",       "Lechuga, tomate, pollo y aderezo César",          "Entrantes",  8.50,  true),
        Dishes(2, "Salmón a la plancha",  "Salmón fresco con guarnición de verduras",         "Principales", 18.00, true),
        Dishes(3, "Pasta Carbonara",      "Pasta con bacon, huevo y queso parmesano",         "Principales", 12.50, true),
        Dishes(4, "Tiramisú",             "Postre italiano con mascarpone y café",            "Postres",    6.00,  true),
        Dishes(5, "Vino de la casa",      "Copa de vino tinto de la casa",                   "Bebidas",    3.50,  true)
    )

    suspend fun getDishes(): List<Dishes> = dishes.toList()

    suspend fun addDish(dish: Dishes): Dishes {
        val withId = dish.copy(id = nextId++)
        dishes.add(withId)
        return withId
    }

    suspend fun updateDish(dish: Dishes) {
        val idx = dishes.indexOfFirst { it.id == dish.id }
        if (idx >= 0) dishes[idx] = dish
    }

    suspend fun deleteDish(id: Int) {
        dishes.removeAll { it.id == id }
    }
}
