package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.OrderHistorical
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.OrderHistoryService
import com.tfg_rm.desktopapp_restaurantmanager.ui.screens.components.UiState
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.nio.channels.UnresolvedAddressException

class OrderHistoryViewModel(
    val service: OrderHistoryService
) : ViewModel() {
    private val _title = MutableStateFlow(Strings.t("screen.orderHistory.title"))
    val title: StateFlow<String> = _title.asStateFlow()

    private val _orders = MutableStateFlow<UiState<List<OrderHistorical>>>(UiState.Idle)
    val orders: StateFlow<UiState<List<OrderHistorical>>> = _orders.asStateFlow()

    fun resetState() {
        _orders.value = UiState.Idle
    }

    fun loadOrderHistory() {
        _orders.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = service.getHistory()
                _orders.value = UiState.Success(result)
            } catch (e: UnresolvedAddressException) {
                _orders.value = UiState.Error(Strings.t("errors.ipadressnotexist"))
                println("Error on loadOrderHistory in OrderHistoryViewModel, direccion ip no existente")
                e.printStackTrace()
            } catch (e: Exception) {
                _orders.value = UiState.Error(Strings.t("errors.ipadressnotexist"))
                e.printStackTrace()
                println("Error on loadOrderHistory in OrderHistoryViewModel")
            }
        }
    }
}
