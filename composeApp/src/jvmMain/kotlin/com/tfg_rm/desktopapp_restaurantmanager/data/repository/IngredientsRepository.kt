package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.InventoryRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper.toIngredient
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper.toIngredientOperationRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.network.SocketManager
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.network.TokenProvider
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Ingredient

class IngredientsRepository(
    private val dataSource: InventoryRemoteDataSource,
    private val tokenProvider: TokenProvider,
    private val socketManager: SocketManager
) {

    suspend fun getIngredients(): List<Ingredient> = dataSource.getIngredients().map { it.toIngredient() }

    fun loadRole(): String? =
        tokenProvider.getRole()

    suspend fun addIngredient(ingredient: Ingredient): Ingredient {
        return dataSource.createIngredient(ingredient.toIngredientOperationRequest()).toIngredient()
    }

    suspend fun updateIngredient(updated: Ingredient) {
        dataSource.updateIngredient(updated.id, updated.toIngredientOperationRequest())
    }

    suspend fun deleteIngredient(id: Int) {
        dataSource.deleteIngredient(id)
    }

    fun observeMessages() = socketManager.messages

    suspend fun sendMessage(message: String) = socketManager.sendMessage(message)

    suspend fun disconnectWS() = socketManager.disconnect()
}
