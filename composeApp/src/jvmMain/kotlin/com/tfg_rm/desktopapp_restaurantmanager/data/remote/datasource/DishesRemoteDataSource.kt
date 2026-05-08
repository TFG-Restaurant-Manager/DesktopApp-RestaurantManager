package com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.DishCreateRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.DishesResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class DishesRemoteDataSource(private val client: HttpClient) {

    suspend fun getDishes(): List<DishesResponse> =
        client.get("api/dish").body()


    suspend fun createDish(request: DishCreateRequest): DishesResponse =
        client.post("") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}
