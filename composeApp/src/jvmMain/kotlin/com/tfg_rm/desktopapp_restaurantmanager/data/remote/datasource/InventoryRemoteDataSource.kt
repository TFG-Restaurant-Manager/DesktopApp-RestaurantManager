package com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.IngredientsDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class InventoryRemoteDataSource(
    private val client: HttpClient
) {

    suspend fun getIngredients(): List<IngredientsDto> = client.get("api/ingredients").body()
}