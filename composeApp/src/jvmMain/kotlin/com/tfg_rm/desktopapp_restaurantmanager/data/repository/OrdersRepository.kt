package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Dishes
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Order
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.OrderItem
import java.time.LocalDateTime

class OrdersRepository {
    suspend fun getOrders(): List<Order> {
        // Platos de ejemplo
        val dish1 = Dishes(1, "Paella", "Arroz con mariscos", "Main", 12.5, true)
        val dish2 = Dishes(2, "Ensalada", "Mixta", "Starter", 5.0, true)
        val dish3 = Dishes(3, "Tarta de queso", "Postre casero", "Dessert", 4.0, true)

        // Items de pedidos
        val item1 = OrderItem(1, dish1, 2, null)
        val item2 = OrderItem(2, dish2, 1, "sin cebolla")
        val item3 = OrderItem(3, dish3, 3, null)
        val item4 = OrderItem(4, dish1, 1, "extra limón")
        val item5 = OrderItem(5, dish2, 2, null)

        // Fecha base: 24/03/2026 17:10
        val baseDay = LocalDateTime.of(2026, 3, 24, 17, 35)

        val order1 = Order(
            id = 1,
            restaurantId = 1,
            tableId = 1,
            status = "OPEN",
            total = (dish1.price * 2 + dish2.price * 1).toFloat(),
            notes = "Urgente",
            createdAt = baseDay.minusMinutes(2), // 17:08
            orderItemsList = mutableListOf(item1, item2)
        )

        val order2 = Order(
            id = 2,
            restaurantId = 1,
            tableId = 2,
            status = "CLOSED",
            total = (dish2.price * 1).toFloat(),
            notes = null,
            createdAt = baseDay.minusHours(1).minusMinutes(40), // 15:30
            orderItemsList = mutableListOf(item2)
        )

        val order3 = Order(
            id = 3,
            restaurantId = 1,
            tableId = 3,
            status = "PREPARING",
            total = (dish3.price * 3 + dish1.price * 1).toFloat(),
            notes = "Sin azúcar en el postre",
            createdAt = baseDay.minusMinutes(25), // 16:45
            orderItemsList = mutableListOf(item3, item4)
        )

        val order4 = Order(
            id = 4,
            restaurantId = 1,
            tableId = 4,
            status = "DELIVERED",
            total = (dish1.price * 1 + dish2.price * 2).toFloat(),
            notes = "Cliente habitual",
            createdAt = baseDay.minusDays(1).withHour(20).withMinute(0), // 23/03/2026 20:00
            orderItemsList = mutableListOf(item4, item5)
        )

        val order5 = Order(
            id = 5,
            restaurantId = 1,
            tableId = 5,
            status = "CANCELLED",
            total = (dish3.price * 1).toFloat(),
            notes = "Cancelado por el cliente",
            createdAt = baseDay.minusDays(2).withHour(11).withMinute(20), // 22/03/2026 11:20
            orderItemsList = mutableListOf(item3)
        )

        val order6 = Order(
            id = 6,
            restaurantId = 1,
            tableId = 6,
            status = "OPEN",
            total = (dish1.price * 1).toFloat(),
            notes = null,
            createdAt = LocalDateTime.of(2026, 3, 20, 12, 0), // 20/03/2026 12:00
            orderItemsList = mutableListOf(item4)
        )

        val order7 = Order(
            id = 7,
            restaurantId = 1,
            tableId = 7,
            status = "CLOSED",
            total = (dish2.price * 2 + dish3.price * 1).toFloat(),
            notes = "Pedido para llevar",
            createdAt = baseDay.withHour(9).withMinute(15), // 24/03/2026 09:15
            orderItemsList = mutableListOf(item5, item3)
        )

        return listOf(order1, order2, order3, order4, order5, order6, order7)
    }
}
