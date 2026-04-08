package com.tfg_rm.desktopapp_restaurantmanager.data.remote

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.OrderCreateRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.OrderCreateResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class OrdersRemoteDataSource(
    private val client: HttpClient
) {
    suspend fun createOrder(request: OrderCreateRequest): OrderCreateResponse {
        return client.post("") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
