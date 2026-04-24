package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.OrdersRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper.toOrderCreateRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper.toOrderHistorical
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.network.SocketManager
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Order
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.OrderHistorical
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

class OrdersRepository(
    private val remote: OrdersRemoteDataSource,
    private val socketManager: SocketManager
) {

    suspend fun getOrders(): List<Order> = remote.getOrders()

    suspend fun addOrder(order: Order) {
        val json = Json {
            encodeDefaults = true
        }
        sendMessage(json.encodeToJsonElement(order.toOrderCreateRequest()).toString())
    }

    suspend fun getOrderHistory(): List<OrderHistorical> = remote.getOrdersHistorical().map { it.toOrderHistorical() }

    suspend fun updateOrder(updated: Order) {
        // In implementation
    }

    fun observeMessages() = socketManager.messages

    suspend fun sendMessage(message: String) {
        println("Mensaje enviado: $message")
        socketManager.sendMessage(message)
    }

    suspend fun disconnectWS() = socketManager.disconnect()
}
