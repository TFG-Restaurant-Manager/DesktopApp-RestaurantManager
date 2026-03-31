package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.domain.NewOrderStep
import com.tfg_rm.desktopapp_restaurantmanager.domain.OrderType
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Dishes
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.DraftItem
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Order
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.OrderItem
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Table
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.DishesService
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.OrdersService
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.TablesService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewOrderViewModel(
    private val dishesService: DishesService,
    private val tablesService: TablesService,
    private val ordersService: OrdersService
) : ViewModel() {

    // Navigation step
    private val _step = MutableStateFlow(NewOrderStep.TYPE)
    val step: StateFlow<NewOrderStep> = _step.asStateFlow()

    // Order type & table selection
    private val _orderType  = MutableStateFlow(OrderType.TABLE)
    val orderType: StateFlow<OrderType> = _orderType.asStateFlow()

    private val _selectedTableId = MutableStateFlow<Int?>(null)
    val selectedTableId: StateFlow<Int?> = _selectedTableId.asStateFlow()

    private val _deliveryAddress = MutableStateFlow("")
    val deliveryAddress: StateFlow<String> = _deliveryAddress.asStateFlow()

    // Tables for the map
    private val _tables = MutableStateFlow<List<Table>>(emptyList())
    val tables: StateFlow<List<Table>> = _tables.asStateFlow()

    // Available dishes
    private val _dishes = MutableStateFlow<List<Dishes>>(emptyList())
    val dishes: StateFlow<List<Dishes>> = _dishes.asStateFlow()

    // Current order items (draft list)
    private val _draftItems = MutableStateFlow<List<DraftItem>>(emptyList())
    val draftItems: StateFlow<List<DraftItem>> = _draftItems.asStateFlow()

    // Counter for stable OrderItem ids
    private var nextItemId = 100

    // The just-confirmed order (used to signal success back to caller)
    private val _lastSubmittedOrder = MutableStateFlow<Order?>(null)
    val lastSubmittedOrder: StateFlow<Order?> = _lastSubmittedOrder.asStateFlow()

    // ─── init ────────────────────────────────────────────────────────────────
    init {
        viewModelScope.launch {
            _tables.value = tablesService.getTables()
            _dishes.value = dishesService.getDishes().filter { it.available }
        }
    }

    // ─── Step 1: type & table ────────────────────────────────────────────────
    fun selectOrderType(type: OrderType) { _orderType.value = type }
    fun selectTable(id: Int) { _selectedTableId.value = id }
    fun setDeliveryAddress(addr: String) { _deliveryAddress.value = addr }

    fun confirmType() {
        if (_orderType.value == OrderType.TABLE && _selectedTableId.value == null) return
        _step.value = NewOrderStep.DISHES
    }

    // ─── Step 2: dishes ──────────────────────────────────────────────────────
    fun addDraftItem(item: DraftItem) {
        val list = _draftItems.value.toMutableList()
        // If same dish already exists, increment quantity instead
        val existing = list.indexOfFirst {
            it.dish.id == item.dish.id && it.notes == item.notes && it.ingredientMods == item.ingredientMods
        }
        if (existing >= 0) {
            list[existing] = list[existing].copy(quantity = list[existing].quantity + item.quantity)
        } else {
            list.add(item)
        }
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

    fun backToType()   { _step.value = NewOrderStep.TYPE }
    fun backToDishes() { _step.value = NewOrderStep.DISHES }

    // ─── Step 3: payment & submit ────────────────────────────────────────────
    /** Builds the final [Order] and pushes it via the service. Returns the saved order. */
    fun submitOrder(paymentMethod: String, cashGiven: Double?, onDone: (Order) -> Unit) {
        viewModelScope.launch {
            val items = _draftItems.value.map { draft ->
                val modNotes = draft.ingredientMods.entries
                    .filter { it.value != "NORMAL" }
                    .joinToString(", ") { (id, mod) ->
                        val ingName = draft.dish.ingredients.firstOrNull { it.ingredient.id == id }?.ingredient?.name ?: "Ingrediente $id"
                        if (mod == "EXTRA") "+$ingName" else "-$ingName"
                    }
                val fullNotes = listOfNotNull(draft.notes.ifBlank { null }, modNotes.ifBlank { null }).joinToString("; ")
                OrderItem(
                    id        = nextItemId++,
                    dish      = draft.dish,
                    unitPrice = draft.dish.price,
                    notes     = fullNotes.ifBlank { null },
                    quantity  = draft.quantity
                )
            }
            val total = items.sumOf { it.unitPrice * it.quantity }
            val order = Order(
                id               = 0,
                restaurantId     = 1,
                tableId          = _selectedTableId.value ?: 0,
                status           = "CREATED",
                total            = total,
                orderType        = _orderType.value.name,
                notes            = if (_orderType.value == OrderType.DELIVERY) _deliveryAddress.value.ifBlank { null } else null,
                deliveryAddress  = if (_orderType.value == OrderType.DELIVERY) _deliveryAddress.value.ifBlank { null } else null,
                orderItemsList   = items.toMutableList()
            )
            val saved = ordersService.addOrder(order)
            _lastSubmittedOrder.value = saved
            onDone(saved)
            reset()
        }
    }

    fun reset() {
        _step.value          = NewOrderStep.TYPE
        _orderType.value     = OrderType.TABLE
        _selectedTableId.value = null
        _deliveryAddress.value = ""
        _draftItems.value    = emptyList()
        _lastSubmittedOrder.value = null
    }
}
