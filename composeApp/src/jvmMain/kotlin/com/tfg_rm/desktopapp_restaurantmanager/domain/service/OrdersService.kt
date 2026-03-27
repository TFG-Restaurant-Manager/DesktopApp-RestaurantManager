package com.tfg_rm.desktopapp_restaurantmanager.domain.service

import com.tfg_rm.desktopapp_restaurantmanager.data.repository.OrdersRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Order

class OrdersService(
    private val repository: OrdersRepository
) {
    suspend fun getOrders(): List<Order> = repository.getOrders()
}
