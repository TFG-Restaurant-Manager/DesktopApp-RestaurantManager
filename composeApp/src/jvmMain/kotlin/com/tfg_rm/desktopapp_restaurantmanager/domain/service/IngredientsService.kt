package com.tfg_rm.desktopapp_restaurantmanager.domain.service

import com.tfg_rm.desktopapp_restaurantmanager.data.repository.IngredientsRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Ingredient

class IngredientsService(
    private val repository: IngredientsRepository
) {
    suspend fun getIngredients(): List<Ingredient> = repository.getIngredients()

    suspend fun addIngredient(ingredient: Ingredient): Ingredient = repository.addIngredient(ingredient)

    suspend fun updateIngredient(ingredient: Ingredient) = repository.updateIngredient(ingredient)

    suspend fun deleteIngredient(id: Int) = repository.deleteIngredient(id)

    fun observeMessages() = repository.observeMessages()

    suspend fun sendMessage(message: String) = repository.sendMessage(message)

    suspend fun disconnectWS() = repository.disconnectWS()
}
