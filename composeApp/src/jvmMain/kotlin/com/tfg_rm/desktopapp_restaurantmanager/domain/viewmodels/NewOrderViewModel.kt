package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.OrderCreatedResponse
import com.tfg_rm.desktopapp_restaurantmanager.domain.NewOrderStep
import com.tfg_rm.desktopapp_restaurantmanager.domain.OrderType
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.*
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.DishesService
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.OrdersService
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.TablesService
import com.tfg_rm.desktopapp_restaurantmanager.ui.screens.components.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.nio.channels.UnresolvedAddressException
import kotlin.coroutines.cancellation.CancellationException

class NewOrderViewModel(
    private val dishesService: DishesService,
    private val tablesService: TablesService,
    private val ordersService: OrdersService
) : ViewModel() {

    // Navigation step
    private val _step = MutableStateFlow(NewOrderStep.TYPE)
    val step: StateFlow<NewOrderStep> = _step.asStateFlow()

    // Order type & table selection
    private val _orderType = MutableStateFlow(OrderType.TABLE)
    val orderType: StateFlow<OrderType> = _orderType.asStateFlow()

    private val _selectedTableId = MutableStateFlow<Table?>(null)
    val selectedTableId: StateFlow<Table?> = _selectedTableId.asStateFlow()

    private val _deliveryAddress = MutableStateFlow("")
    val deliveryAddress: StateFlow<String> = _deliveryAddress.asStateFlow()

    // Tables for the map
    private val _tables = MutableStateFlow<UiState<List<Table>>>(UiState.Idle)
    val tables: StateFlow<UiState<List<Table>>> = _tables.asStateFlow()

    // Available dishes
    private val _dishes = MutableStateFlow<UiState<List<Dishes>>>(UiState.Idle)
    val dishes: StateFlow<UiState<List<Dishes>>> = _dishes.asStateFlow()

    // Current order items (draft list)
    private val _draftItems = MutableStateFlow<List<DraftItem>>(emptyList())
    val draftItems: StateFlow<List<DraftItem>> = _draftItems.asStateFlow()

    // The just-confirmed order (used to signal success back to caller)
    private val _lastSubmittedOrder = MutableStateFlow<Order?>(null)
    val lastSubmittedOrder: StateFlow<Order?> = _lastSubmittedOrder.asStateFlow()

    // ─── init ────────────────────────────────────────────────────────────────
    init {
        loadData()
    }

    fun loadData() {
        _tables.value = UiState.Loading
        _dishes.value = UiState.Loading
        viewModelScope.launch {
            try {
                val tables = tablesService.getTables()
                val dishes = dishesService.getDishes().filter { it.available }
                observeSocketMessages()
                _tables.value = UiState.Success(tables)
                _dishes.value = UiState.Success(dishes)
            } catch (e: UnresolvedAddressException) {
                println("Error on init in NewOrderViewModel, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on init in NewOrderViewModel")
            }
        }
    }

    // ─── Step 1: type & table ────────────────────────────────────────────────
    fun selectOrderType(type: OrderType) {
        _orderType.value = type
    }

    fun selectTable(table: Table) {
        _selectedTableId.value = table
    }

    fun setDeliveryAddress(addr: String) {
        _deliveryAddress.value = addr
    }

    fun confirmType() {
        if (_orderType.value == OrderType.TABLE && _selectedTableId.value == null) return
        _step.value = NewOrderStep.DISHES
    }

    // ─── Step 2: dishes ──────────────────────────────────────────────────────
    fun addDraftItem(item: DraftItem) {
        val list = _draftItems.value.toMutableList()
        // If same dish already exists, increment quantity instead
        list.add(item)
        _draftItems.value = list
    }

    fun removeDraftItem(index: Int) {
        val list = _draftItems.value.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            _draftItems.value = list
        }
    }

    fun proceedToPayment() {
        if (_draftItems.value.isEmpty()) return
        _step.value = NewOrderStep.PAYMENT
    }

    fun backToType() {
        _step.value = NewOrderStep.TYPE
    }

    fun backToDishes() {
        _step.value = NewOrderStep.DISHES
    }

    // ─── Step 3: payment & submit ────────────────────────────────────────────
    /** Builds the final [Order] and pushes it via the service. Returns the saved order. */
    fun submitOrder() {
        viewModelScope.launch {
            try {
                val items = _draftItems.value.map { draft ->
                    val modNotes = draft.ingredientMods.entries
                        .filter { it.value != "NORMAL" }
                        .joinToString(", ") { (id, mod) ->
                            val ingName =
                                draft.dish.ingredients.firstOrNull { it.ingredient.id == id }?.ingredient?.name
                                    ?: "Ingrediente $id"
                            if (mod == "EXTRA") "+$ingName" else "-$ingName"
                        }
                    val fullNotes =
                        listOfNotNull(draft.notes.ifBlank { null }, modNotes.ifBlank { null }).joinToString("; ")
                    OrderItem(
                        id = 0,
                        dishId = draft.dish.id,
                        unitPrice = draft.dish.price,
                        notes = fullNotes.ifBlank { null },
                        quantity = draft.quantity,
                        dishName = draft.dish.name,
                        status = "CREATED"
                    )
                }
                val total = items.sumOf { it.unitPrice * it.quantity }
                val order = Order(
                    id = 0,
                    tableId = _selectedTableId.value?.id ?: 0,
                    status = "CREATED",
                    total = total,
                    orderType = _orderType.value.name,
                    notes = if (_orderType.value == OrderType.DELIVERY) _deliveryAddress.value.ifBlank { null } else null,
                    deliveryAddress = if (_orderType.value == OrderType.DELIVERY) _deliveryAddress.value.ifBlank { null } else null,
                    orderItemsList = items.toMutableList(),
                    type = _orderType.value.name
                )
                _step.value = NewOrderStep.SENDED
                ordersService.addOrder(order)
                _step.value = NewOrderStep.SENDOK
                reset()
            } catch (e: UnresolvedAddressException) {
                println("Error on submitOrder in NewOrderViewModel, direccion ip no existente")
                reset()
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on submitOrder in NewOrderViewModel")
                reset()
            }
        }
    }

    private fun observeSocketMessages() {
        viewModelScope.launch {
            try {
                ordersService.observeMessages().collect { message ->
                    println("Mensaje recibido en OrdersViewModel: $message")
                    when {
                        message.contains("ORDER_CREATED") -> {
                            val result = Json.decodeFromString<OrderCreatedResponse>(message)
                            println(result.toString())//Falta implementacion tanto del back como de desktop y móvil
                        }

                        message.contains("STATE_ORDER_UPDATE") -> {
                            val result = Json.decodeFromString<OrderCreatedResponse>(message)
                            println(result.toString())//Falta implementacion tanto del back como de desktop y móvil
                        }

                        message.contains("FAILED_CREATE_ORDER") -> {
                            println("Error al crear la orden FAILED_CREATE_ORDER")
                        }

                        message.contains("FAILED_UNHANDLED_TYPE") -> {
                            println("Error al crear la orden FAILED_UNHANDLED_TYPE")
                        }

                        message.contains("FAILED_UNHANDLED_MESSAGE") -> {
                            println("Error al crear la orden FAILED_UNHANDLED_MESSAGE")
                        }
                    }
                }
            } catch (_: CancellationException) {
                ordersService.disconnectWS()
            } catch (e: Exception) {
                e.printStackTrace()
                println(e.message)
            }
        }
    }

    fun reset() {
        _step.value = NewOrderStep.TYPE
        _orderType.value = OrderType.TABLE
        _selectedTableId.value = null
        _deliveryAddress.value = ""
        _draftItems.value = emptyList()
        _lastSubmittedOrder.value = null
    }
}
