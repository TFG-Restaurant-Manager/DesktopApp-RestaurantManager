package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Ingredient

class IngredientsRepository {

    private var nextId = 11

    private val categories = mutableListOf(
        "Verduras", "Carnes", "Pescados", "Secos", "Condimentos", "Lácteos", "Bebidas"
    )

    private val ingredients = mutableListOf(
        Ingredient(1,  1, "Tomates",         "kg",       45.0, 2.50,  "Verduras",    20.0),
        Ingredient(2,  1, "Lechuga",          "unidades", 12.0, 1.20,  "Verduras",    15.0),
        Ingredient(3,  1, "Pollo",            "kg",       35.0, 6.80,  "Carnes",      25.0),
        Ingredient(4,  1, "Salmón",           "kg",       18.0, 15.50, "Pescados",    10.0),
        Ingredient(5,  1, "Pasta",            "kg",       75.0, 1.80,  "Secos",       30.0),
        Ingredient(6,  1, "Aceite de Oliva",  "litros",    8.0, 8.50,  "Condimentos", 12.0),
        Ingredient(7,  1, "Queso Parmesano",  "kg",       22.0, 18.00, "Lácteos",     15.0),
        Ingredient(8,  1, "Vino Tinto",       "botellas", 48.0, 7.50,  "Bebidas",     30.0),
        Ingredient(9,  1, "Harina",           "kg",       55.0, 0.90,  "Secos",       40.0),
        Ingredient(10, 1, "Cebolla",          "kg",       28.0, 1.50,  "Verduras",    20.0)
    )

    suspend fun getIngredients(): List<Ingredient> = ingredients.toList()

    suspend fun getCategories(): List<String> = categories.toList()

    suspend fun addIngredient(ingredient: Ingredient): Ingredient {
        val withId = ingredient.copy(id = nextId++)
        ingredients.add(withId)
        return withId
    }

    suspend fun updateIngredient(updated: Ingredient) {
        val idx = ingredients.indexOfFirst { it.id == updated.id }
        if (idx >= 0) ingredients[idx] = updated
    }

    suspend fun deleteIngredient(id: Int) {
        ingredients.removeAll { it.id == id }
    }

    suspend fun addCategory(name: String) {
        if (!categories.contains(name)) categories.add(name)
    }

    suspend fun deleteCategory(name: String) {
        categories.remove(name)
    }
}
