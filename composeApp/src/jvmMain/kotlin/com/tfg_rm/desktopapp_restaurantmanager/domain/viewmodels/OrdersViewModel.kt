package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.network.SessionManager
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.FlatEntry
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Order
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.OrdersService
import com.tfg_rm.desktopapp_restaurantmanager.ui.screens.components.UiState
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.nio.channels.UnresolvedAddressException
import java.time.Duration
import java.time.LocalDateTime
import kotlin.coroutines.cancellation.CancellationException

class OrdersViewModel(
    val service: OrdersService
) : ViewModel() {
    private val _title = MutableStateFlow(Strings.t("screen.orders.title"))
    val title: StateFlow<String> = _title.asStateFlow()

    private val _orders = MutableStateFlow<UiState<List<Order>>>(UiState.Idle)
    val orders: StateFlow<UiState<List<Order>>> = _orders.asStateFlow()

    init {
        viewModelScope.launch {
            SessionManager.sessionExpired.collect {
                resetState()
            }
        }
    }

    fun resetState() {
        service.clearCache()
        _orders.value = UiState.Idle
    }

    fun loadOrders() {
        _orders.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = service.getOrders()
                observeSocketMessages()
                _orders.value = UiState.Success(result)
            } catch (_: UnresolvedAddressException) {
                _orders.value = UiState.Error(Strings.t("errors.ipadressnotexist"))
                println("Error on loadOrders in OrdersViewModel, direccion ip no existente")
            } catch (e: Exception) {
                _orders.value = UiState.Error(Strings.t("errors.undefined"))
                e.printStackTrace()
                println("Error on loadOrders in OrdersViewModel")
            }
        }
    }

    /** Called by NewOrderScreen to push a finished order into the active list. */
    // Unused for now, may be used in the near future
//    fun addOrder(order: Order) {
//        viewModelScope.launch {
//            try {
//                service.addOrder(order)
//                _orders.update { state ->
//                    if (state is UiState.Success) {
//                        UiState.Success(state.data + order)
//                    } else state
//                }
//            } catch (_: UnresolvedAddressException) {
//                println("Error on addOrder in OrdersViewModel, direccion ip no existente")
//            } catch (e: Exception) {
//                e.printStackTrace()
//                println("Error on addOrder in OrdersViewModel")
//            }
//        }
//    }

    fun elapsedMinutes(order: Order): Long = try {
        Duration.between(order.createdAt, LocalDateTime.now()).toMinutes()
    } catch (_: Exception) {
        0
    }

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
                val updated = (_orders.value as UiState.Success).data.mapNotNull { order ->
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
                _orders.value = UiState.Success(updated)
            } catch (_: UnresolvedAddressException) {
                println("Error on addOrder in OrdersViewModel, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on addOrder in OrdersViewModel")
            }
        }
    }

    private fun observeSocketMessages() {
        viewModelScope.launch {
            try {
                service.observeMessages().collect { message ->
                    println("Mensaje recibido en DishesViewModel: $message")
                }
            } catch (_: CancellationException) {
                service.disconnectWS()
            } catch (e: Exception) {
                e.printStackTrace()
                println(e.message)
            }
        }
    }
}
