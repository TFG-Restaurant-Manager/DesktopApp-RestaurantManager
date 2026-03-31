package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Dishes

class DishesRepository {
    private var nextId = 6

    private val dishes = mutableListOf(
        Dishes(1, name = "Ensalada César",      description = "Lechuga, tomate, pollo y aderezo César",  categoryName = "Entrantes",   price = 8.50,  available = true),
        Dishes(2, name = "Salmón a la plancha", description = "Salmón fresco con guarnición de verduras", categoryName = "Principales", price = 18.00, available = true),
        Dishes(3, name = "Pasta Carbonara",     description = "Pasta con bacon, huevo y queso parmesano", categoryName = "Principales", price = 12.50, available = true),
        Dishes(4, name = "Tiramisú",            description = "Postre italiano con mascarpone y café",    categoryName = "Postres",     price = 6.00,  available = true),
        Dishes(5, name = "Vino de la casa",     description = "Copa de vino tinto de la casa",            categoryName = "Bebidas",     price = 3.50,  available = true)
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
