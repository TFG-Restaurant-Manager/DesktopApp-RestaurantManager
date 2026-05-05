package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.OrderCreatedResponse
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.OrderUpdatedResponse
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper.toOrder
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.network.SessionManager
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.FlatEntry
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Order
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.OrderItem
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.OrdersService
import com.tfg_rm.desktopapp_restaurantmanager.ui.screens.components.UiState
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.nio.channels.UnresolvedAddressException
import java.time.Duration
import java.time.LocalDateTime
import kotlin.coroutines.cancellation.CancellationException

class OrdersViewModel(
    val service: OrdersService
) : ViewModel() {

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
                val state = _orders.value
                if (state is UiState.Success<List<Order>>) {
                    val orderModified = state.data.find { it.id == orderId }

                    if (orderModified != null) {
                        if (orderModified.orderItemsList.find { item -> item.id == itemId } != null) {
                            val updatedItems = orderModified.orderItemsList.map {
                                if (it.id == itemId) {
                                    it.copy(status = "COOKED")
                                } else it
                            }

                            val updatedOrder =
                                orderModified.copy(orderItemsList = updatedItems as MutableList<OrderItem>)

                            service.updateOrder(updatedOrder)
                        } else println("Error, no existe ese order item a completar")
                    } else println("Error, no existe esa orden a completar")
                }
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
                    println("Mensaje recibido en OrdersViewModel: $message")
                    when {
                        message.contains("ORDER_CREATED") -> {
                            val result = Json.decodeFromString<OrderCreatedResponse>(message)
                            println("Mensaje websocket, orden creada")
                            _orders.update { state ->
                                if (state is UiState.Success) {
                                    UiState.Success(state.data + result.payload.toOrder())
                                } else state
                            }
                        }

                        message.contains("ORDER_UPDATED") -> {
                            val result = Json.decodeFromString<OrderUpdatedResponse>(message)
                            val orderModified = result.payload.toOrder()
                            _orders.update { state ->
                                if (state is UiState.Success) {
                                    UiState.Success(state.data.map { currentOrder ->
                                        if (currentOrder.id == orderModified.id) {
                                            orderModified
                                        } else {
                                            currentOrder
                                        }
                                    })
                                } else state
                            }
                            println("Mensaje websocket, orden modificada")
                        }

                        message.contains("FAILED_CREATE_ORDER") -> {
                            println("Error al crear la orden FAILED_CREATE_ORDER")
                        }

                        message.contains("FAILED_UNHANDLED_MESSAGE ") -> {
                            println("Mensaje erroneo, no tiene formato del json requerido")
                        }

                        message.contains("FAILED_UNKNOWN_TYPE") -> {
                            println("Error desconocido")
                        }
                    }
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
