package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.InventoryRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper.toIngredient
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.network.SocketManager
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Ingredient

class IngredientsRepository(
    private val dataSource: InventoryRemoteDataSource,
    private val socketManager: SocketManager
) {

    suspend fun getIngredients(): List<Ingredient> = dataSource.getIngredients().map { it.toIngredient() }

    suspend fun addIngredient(ingredient: Ingredient) {
        // Misses implementation
    }

    suspend fun updateIngredient(updated: Ingredient) {
        // MIsses implementation
    }

    suspend fun deleteIngredient(id: Int) {
        // Misses implementation
    }

    fun observeMessages() = socketManager.messages

    suspend fun sendMessage(message: String) = socketManager.sendMessage(message)

    suspend fun disconnectWS() = socketManager.disconnect()
}
