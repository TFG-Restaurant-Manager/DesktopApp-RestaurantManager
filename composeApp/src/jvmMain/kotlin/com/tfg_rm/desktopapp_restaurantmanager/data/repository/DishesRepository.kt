package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.DishesRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper.toDishCreateRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper.toDishes
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper.toDishesUpdateRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.network.SocketManager
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.network.TokenProvider
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Dishes

class DishesRepository(
    private val remoteDataSource: DishesRemoteDataSource,
    private val tokenProvider: TokenProvider,
    private val socketManager: SocketManager
) {

    suspend fun getDishes(): List<Dishes> = remoteDataSource.getDishes().map { it.toDishes() }

    suspend fun addDish(dish: Dishes) {
        val request = dish.toDishCreateRequest()
        remoteDataSource.createDish(request)
    }

    suspend fun updateDish(dish: Dishes) {
        remoteDataSource.updateDish(dish.toDishesUpdateRequest(), dish.id)
    }

    suspend fun deleteDish(id: Int) {
        remoteDataSource.deleteDish(id)
    }

    fun loadRole(): String? =
        tokenProvider.getRole()

    fun observeMessages() = socketManager.messages

    suspend fun sendMessage(message: String) = socketManager.sendMessage(message)

    suspend fun disconnectWS() = socketManager.disconnect()
}
