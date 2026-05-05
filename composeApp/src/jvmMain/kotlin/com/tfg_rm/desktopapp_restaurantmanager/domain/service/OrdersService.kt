package com.tfg_rm.desktopapp_restaurantmanager.domain.service

import com.tfg_rm.desktopapp_restaurantmanager.data.repository.OrdersRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Order

class OrdersService(
    private val repository: OrdersRepository
) {
    suspend fun getOrders(): List<Order> = repository.getOrders()

    suspend fun addOrder(order: Order) = repository.addOrder(order)
    suspend fun updateOrder(order: Order) = repository.updateOrder(order)

    fun observeMessages() = repository.observeMessages()

    suspend fun sendMessage(message: String) = repository.sendMessage(message)

    suspend fun disconnectWS() = repository.disconnectWS()
}
