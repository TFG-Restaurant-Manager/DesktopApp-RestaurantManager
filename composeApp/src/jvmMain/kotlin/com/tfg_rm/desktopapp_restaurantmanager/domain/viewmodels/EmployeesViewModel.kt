package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EmployeesViewModel : ViewModel() {
    private val _title = MutableStateFlow(Strings.t("screen.employees.title"))
    val title: StateFlow<String> = _title.asStateFlow()
}
