package com.tfg_rm.desktopapp_restaurantmanager.domain.service

import com.tfg_rm.desktopapp_restaurantmanager.data.repository.DishesRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.IngredientsRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Dishes
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Ingredient

class DishesService(
    private val dishesRepository: DishesRepository,
    private val ingredientsRepository: IngredientsRepository
) {
    suspend fun getDishes(): List<Dishes>           = dishesRepository.getDishes()
    suspend fun getIngredients(): List<Ingredient> = ingredientsRepository.getIngredients()
        .filter { it.usableInDishes }
    suspend fun addDish(dish: Dishes)       = dishesRepository.addDish(dish)
    suspend fun updateDish(dish: Dishes)            = dishesRepository.updateDish(dish)
    suspend fun deleteDish(id: Int)                 = dishesRepository.deleteDish(id)
}
