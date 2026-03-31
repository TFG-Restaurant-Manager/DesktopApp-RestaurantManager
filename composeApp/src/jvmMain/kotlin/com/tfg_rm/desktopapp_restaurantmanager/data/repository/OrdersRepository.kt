package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Dishes
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Order
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.OrderItem
import java.time.LocalDateTime

class OrdersRepository {

    private var nextId = 8
    private val activeOrders = mutableListOf<Order>()

    init {
        // seed sample data
        val dish1 = Dishes(1, name = "Paella",         description = "Arroz con mariscos", categoryName = "Principales", price = 12.5, available = true)
        val dish2 = Dishes(2, name = "Ensalada",        description = "Mixta",               categoryName = "Entrantes",   price = 5.0,  available = true)
        val dish3 = Dishes(3, name = "Tarta de queso", description = "Postre casero",        categoryName = "Postres",     price = 4.0,  available = true)
        val item1 = OrderItem(1, dish1, unitPrice = dish1.price, quantity = 2)
        val item2 = OrderItem(2, dish2, unitPrice = dish2.price, quantity = 1, notes = "sin cebolla")
        val item3 = OrderItem(3, dish3, unitPrice = dish3.price, quantity = 3)
        val item4 = OrderItem(4, dish1, unitPrice = dish1.price, quantity = 1, notes = "extra limón")
        val item5 = OrderItem(5, dish2, unitPrice = dish2.price, quantity = 2)
        val base   = LocalDateTime.of(2026, 3, 24, 17, 35)
        activeOrders += Order(1, 1, 1, "CREATED",   dish1.price*2+dish2.price, notes="Urgente",                  createdAt=base.minusMinutes(2),                          orderItemsList=mutableListOf(item1,item2))
        activeOrders += Order(2, 1, 2, "COOKED",    dish2.price,                notes=null,                      createdAt=base.minusHours(1).minusMinutes(40),            orderItemsList=mutableListOf(item2))
        activeOrders += Order(3, 1, 3, "CREATED",   dish3.price*3+dish1.price,  notes="Sin azúcar en el postre", createdAt=base.minusMinutes(25),                          orderItemsList=mutableListOf(item3,item4))
        activeOrders += Order(4, 1, 4, "DELIVERED", dish1.price+dish2.price*2,  notes="Cliente habitual",        createdAt=base.minusDays(1).withHour(20).withMinute(0),   orderItemsList=mutableListOf(item4,item5))
    }

    suspend fun getOrders(): List<Order> = activeOrders.toList()

    suspend fun addOrder(order: Order): Order {
        val withId = order.copy(id = nextId++)
        activeOrders.add(withId)
        return withId
    }

    suspend fun updateOrder(updated: Order) {
        val idx = activeOrders.indexOfFirst { it.id == updated.id }
        if (idx >= 0) activeOrders[idx] = updated
    }
}
