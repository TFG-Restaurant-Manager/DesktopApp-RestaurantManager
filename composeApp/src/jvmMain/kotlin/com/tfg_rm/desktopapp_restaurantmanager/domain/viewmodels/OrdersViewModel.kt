package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.OrdersService
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.FlatEntry
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Order
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.nio.channels.UnresolvedAddressException
import java.time.Duration
import java.time.LocalDateTime

class OrdersViewModel(
    val service: OrdersService
) : ViewModel() {
    private val _title = MutableStateFlow(Strings.t("screen.orders.title"))
    val title: StateFlow<String> = _title.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    fun loadOrders() {
        viewModelScope.launch {
            try {
                _orders.value = service.getOrders()
            }catch (e: UnresolvedAddressException) {
                println("Error on loadOrders in OrdersViewModel, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on loadOrders in OrdersViewModel")
            }
        }
    }

    fun clear() { }

    /** Called by NewOrderScreen to push a finished order into the active list. */
    fun addOrder(order: Order) {
        viewModelScope.launch {
            try {
                service.addOrder(order)
                _orders.value = service.getOrders()
            }catch (e: UnresolvedAddressException) {
                println("Error on addOrder in OrdersViewModel, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on addOrder in OrdersViewModel")
            }
        }
    }

    /** Append extra items to an existing order (used when re-opening a pending order). */
    fun appendItems(orderId: Int, newItems: List<com.tfg_rm.desktopapp_restaurantmanager.domain.models.OrderItem>) {
        viewModelScope.launch {
            try {
                val order = _orders.value.firstOrNull { it.id == orderId } ?: return@launch
                val merged = order.orderItemsList.toMutableList().also { it.addAll(newItems) }
                val updated = order.copy(
                    total = merged.sumOf { it.unitPrice * it.quantity },
                    orderItemsList = merged
                )
                service.updateOrder(updated)
                _orders.value = service.getOrders()
            } catch (e: UnresolvedAddressException) {
                println("Error on addOrder in OrdersViewModel, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on addOrder in OrdersViewModel")
            }
        }
    }

    fun elapsedMinutes(order: Order): Long = try {
        Duration.between(order.createdAt, LocalDateTime.now()).toMinutes()
    } catch (e: Exception) { 0 }

    fun elapsedSecondsPart(order: Order): Long = try {
        Duration.between(order.createdAt, LocalDateTime.now()).seconds % 60
    } catch (e: Exception) { 0 }

    fun averageTimeLabel(orders: List<Order>): String {
        if (orders.isEmpty()) return "0 min"
        val avg = orders.map { elapsedMinutes(it) }.average().toInt()
        return "$avg min"
    }

    fun buildFlatEntries(orders: List<Order>): List<FlatEntry> =
        orders
            .flatMap { order -> order.orderItemsList.flatMap { item -> List(item.quantity) { order to item } } }
            .mapIndexed { idx, (order, item) -> FlatEntry(idx + 1, order, item) }

    fun completeOrderItem(orderId: Int, itemId: Int) {
        viewModelScope.launch {
            try {
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
            } catch (e: UnresolvedAddressException) {
                println("Error on addOrder in OrdersViewModel, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on addOrder in OrdersViewModel")
            }
        }
    }
}
