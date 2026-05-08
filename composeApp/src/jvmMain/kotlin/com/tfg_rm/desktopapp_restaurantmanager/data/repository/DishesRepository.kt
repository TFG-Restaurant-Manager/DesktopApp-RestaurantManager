package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.DishesRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.DishCreateRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.DishIngredientRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper.toDishCreateRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper.toDishes
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Dishes

class DishesRepository(
    private val remoteDataSource: DishesRemoteDataSource
) {

    suspend fun getDishes(): List<Dishes> = remoteDataSource.getDishes().map { it.toDishes() }

    suspend fun addDish(dish: Dishes) {
        val request = dish.toDishCreateRequest()
        remoteDataSource.createDish(request)
    }

    suspend fun updateDish(dish: Dishes) {
        // Make it with the repository
    }

    suspend fun deleteDish(id: Int) {
        // Make it with the repository
    }
}
