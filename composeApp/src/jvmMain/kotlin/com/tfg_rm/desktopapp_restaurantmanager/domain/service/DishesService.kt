package com.tfg_rm.desktopapp_restaurantmanager.domain.service

import com.tfg_rm.desktopapp_restaurantmanager.data.repository.DishesRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.IngredientsRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Dishes
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Ingredient

class DishesService(
    private val repository: DishesRepository,
    private val ingredientsRepository: IngredientsRepository
) {
    suspend fun getDishes(): List<Dishes> = repository.getDishes()

    fun loadRole(): String? =
        repository.loadRole()

    suspend fun getIngredients(): List<Ingredient> = ingredientsRepository.getIngredients()
        .filter { it.usableInDishes }

    suspend fun addDish(dish: Dishes) = repository.addDish(dish)
    suspend fun updateDish(dish: Dishes) = repository.updateDish(dish)
    suspend fun deleteDish(id: Int) = repository.deleteDish(id)

    fun observeMessages() = repository.observeMessages()

    suspend fun sendMessage(message: String) = repository.sendMessage(message)

    suspend fun disconnectWS() = repository.disconnectWS()
}
