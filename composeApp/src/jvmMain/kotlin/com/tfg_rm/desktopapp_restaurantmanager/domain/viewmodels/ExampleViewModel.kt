package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.ExampleService
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExampleViewModel(
    val service: ExampleService
) : ViewModel() {
    private val _greeting = MutableStateFlow(Strings.t("main.placeholder"))
    val greeting: StateFlow<String> = _greeting.asStateFlow()

    // Método de ejemplo sin corrutinas — mantiene la forma para Koin
    fun loadGreeting() {
        viewModelScope.launch {
            _greeting.value = service.getGreeting()
        }
    }

    fun clear() {

    }
}
