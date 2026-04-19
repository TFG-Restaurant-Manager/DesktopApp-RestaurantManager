package com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.IngredientOperationRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.IngredientsDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class InventoryRemoteDataSource(
    private val client: HttpClient
) {

    suspend fun getIngredients(): List<IngredientsDto> = client.get("api/ingredients").body()

    suspend fun updateIngredient(ingredientId: Int, request: IngredientOperationRequest) =
        client.put("api/ingredients/$ingredientId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

    suspend fun deleteIngredient(ingredientId: Int) =
        client.delete("api/ingredients/$ingredientId")

    suspend fun createIngredient(request: IngredientOperationRequest) =
        client.post("api/ingredients") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
}