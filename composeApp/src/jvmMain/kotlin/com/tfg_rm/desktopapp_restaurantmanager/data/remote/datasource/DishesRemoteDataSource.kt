package com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.DishCreateRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.DishesResponse
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.DishesUpdateRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class DishesRemoteDataSource(private val client: HttpClient) {

    suspend fun getDishes(): List<DishesResponse> =
        client.get("api/dish").body()


    suspend fun createDish(request: DishCreateRequest): DishesResponse =
        client.post("api/dish") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun deleteDish(dishId: Int) =
        client.delete("api/dish/$dishId")

    suspend fun updateDish(request: DishesUpdateRequest, id: Int) =
        client.put("api/dish/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
}
