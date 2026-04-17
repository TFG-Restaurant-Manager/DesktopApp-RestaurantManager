package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.OrdersRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.OrderCreateRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.OrderItemRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper.toOrderHistorical
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.network.SocketManager
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Order
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.OrderHistorical

class OrdersRepository(
    private val remote: OrdersRemoteDataSource,
    private val socketManager: SocketManager
) {

    suspend fun getOrders(): List<Order> = remote.getOrders()

    suspend fun addOrder(order: Order): Order {
        return if (order.orderType == "TABLE") {
            // Enviar al backend
            val request = OrderCreateRequest(
                type = order.orderType,
                tableId = order.tableId.toLong(),
                notes = order.notes,
                items = order.orderItemsList.map { item ->
                    OrderItemRequest(
                        dishId = item.dishId.toLong(),
                        notes = item.notes,
                        unitPrice = item.unitPrice
                    )
                }
            )
            val response = remote.createOrder(request)
            val saved = order.copy(id = response.id.toInt(), status = response.status)
            saved
        } else {
            // Resto de tipos: local por ahora
            val withId = order.copy(id = 10)
            withId
        }
    }

    suspend fun getOrderHistory(): List<OrderHistorical> = remote.getOrdersHistorical().map { it.toOrderHistorical() }

    suspend fun updateOrder(updated: Order) {
        // In implementation
    }

    fun observeMessages() = socketManager.messages

    suspend fun sendMessage(message: String) = socketManager.sendMessage(message)

    suspend fun disconnectWS() = socketManager.disconnect()
}
