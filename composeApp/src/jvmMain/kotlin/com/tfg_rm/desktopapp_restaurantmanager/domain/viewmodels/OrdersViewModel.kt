package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OrdersViewModel {
    private val _title = MutableStateFlow(Strings.t("screen.orders.title"))
    val title: StateFlow<String> = _title.asStateFlow()
}
