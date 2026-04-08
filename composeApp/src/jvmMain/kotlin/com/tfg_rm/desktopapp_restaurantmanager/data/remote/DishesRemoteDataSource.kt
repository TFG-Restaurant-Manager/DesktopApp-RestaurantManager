package com.tfg_rm.desktopapp_restaurantmanager.data.remote

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.DishCreateRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.DishesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class DishesRemoteDataSource(private val client: HttpClient) {

    suspend fun createDish(request: DishCreateRequest): DishesResponse =
        client.post("") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}
