package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import com.tfg_rm.desktopapp_restaurantmanager.data.repository.ExampleRepository
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExampleViewModel(private val repository: ExampleRepository) {
    private val _greeting = MutableStateFlow(Strings.t("main.placeholder"))
    val greeting: StateFlow<String> = _greeting.asStateFlow()

    // Método de ejemplo sin corrutinas — mantiene la forma para Koin
    fun loadGreeting() {
        _greeting.value = Strings.t("service.hello")
    }

    fun clear() {

    }
}
