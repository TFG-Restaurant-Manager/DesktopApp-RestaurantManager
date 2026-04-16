package com.tfg_rm.desktopapp_restaurantmanager.domain.service

import com.tfg_rm.desktopapp_restaurantmanager.data.repository.OrdersRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.OrderHistorical

class OrderHistoryService(
    private val ordersRepository: OrdersRepository
) {
    suspend fun getHistory(): List<OrderHistorical> = ordersRepository.getOrderHistory()
}
