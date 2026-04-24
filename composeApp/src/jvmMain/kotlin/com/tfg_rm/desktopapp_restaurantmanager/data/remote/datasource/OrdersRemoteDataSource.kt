package com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.OrderCreateResponse
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.OrderRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.OrderResponse
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Order
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class OrdersRemoteDataSource(
    private val client: HttpClient
) {

    suspend fun getOrders(): List<Order> = listOf()

    suspend fun getOrdersHistorical(): List<OrderResponse> = client.get(
        "api/order/paid"
    ).body()

    suspend fun createOrder(request: OrderRequest): OrderCreateResponse {
        return client.post("") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
