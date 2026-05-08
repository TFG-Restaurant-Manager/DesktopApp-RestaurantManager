package com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.OrderResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class OrdersRemoteDataSource(
    private val client: HttpClient
) {
    suspend fun getOrders(): List<OrderResponse> =
        client.get("api/order").body()
}
