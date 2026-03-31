package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.OrdersService
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Order
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrdersViewModel(
    val service: OrdersService
) : ViewModel() {
    private val _title = MutableStateFlow(Strings.t("screen.orders.title"))
    val title: StateFlow<String> = _title.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    fun loadOrders() {
        viewModelScope.launch {
            _orders.value = service.getOrders()
        }
    }

    fun clear() { }

    /** Called by NewOrderScreen to push a finished order into the active list. */
    fun addOrder(order: Order) {
        viewModelScope.launch {
            service.addOrder(order)
            _orders.value = service.getOrders()
        }
    }

    /** Append extra items to an existing order (used when re-opening a pending order). */
    fun appendItems(orderId: Int, newItems: List<com.tfg_rm.desktopapp_restaurantmanager.domain.models.OrderItem>) {
        viewModelScope.launch {
            val order = _orders.value.firstOrNull { it.id == orderId } ?: return@launch
            val merged = order.orderItemsList.toMutableList().also { it.addAll(newItems) }
            val updated = order.copy(
                total = merged.sumOf { it.unitPrice * it.quantity },
                orderItemsList = merged
            )
            service.updateOrder(updated)
            _orders.value = service.getOrders()
        }
    }

    fun completeOrderItem(orderId: Int, itemId: Int) {
        viewModelScope.launch {
            val updated = _orders.value.mapNotNull { order ->
                if (order.id != orderId) return@mapNotNull order
                val newItems = order.orderItemsList.toMutableList()
                val idx = newItems.indexOfFirst { it.id == itemId }
                if (idx >= 0) {
                    val it = newItems[idx]
                    if (it.quantity > 1) {
                        newItems[idx] = it.copy(quantity = it.quantity - 1)
                    } else {
                        newItems.removeAt(idx)
                    }
                }
                if (newItems.isEmpty()) null else order.copy(orderItemsList = newItems)
            }
            _orders.value = updated
        }
    }
}
