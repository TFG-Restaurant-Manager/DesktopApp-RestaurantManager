package com.tfg_rm.desktopapp_restaurantmanager.domain.service

import com.tfg_rm.desktopapp_restaurantmanager.data.repository.OrdersRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Order

class OrderHistoryService(
    private val ordersRepository: OrdersRepository
) {
    suspend fun getHistory(): List<Order> = ordersRepository.getOrderHistory()
}
