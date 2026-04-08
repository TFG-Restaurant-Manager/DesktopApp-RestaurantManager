package com.tfg_rm.desktopapp_restaurantmanager.domain.service

import com.tfg_rm.desktopapp_restaurantmanager.data.repository.OrdersRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.TablesOrdersRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Order
import java.time.LocalDateTime

class OrdersService(
    private val repository: OrdersRepository,
    private val repositoryDuo: TablesOrdersRepository
) {
    suspend fun getOrders(): List<Order> = repositoryDuo.getTablesAndOrders()
        .filter { it.orderId != null }
        .groupBy { it.orderId }
        .map { (_, items) ->

            val first = items.first()

            Order(
                id = first.orderId!!,
                tableId = first.tableId,
                status = first.orderStatus!!,
                total = first.orderTotal!!,
                notes = first.orderNotes,
                createdAt = LocalDateTime.parse(first.orderCreatedAt!!),
                orderItemsList = first.orderItems!!.toMutableList()
            )
        }

    suspend fun addOrder(order: Order): Order = repository.addOrder(order)
    suspend fun updateOrder(order: Order) = repository.updateOrder(order)
}
