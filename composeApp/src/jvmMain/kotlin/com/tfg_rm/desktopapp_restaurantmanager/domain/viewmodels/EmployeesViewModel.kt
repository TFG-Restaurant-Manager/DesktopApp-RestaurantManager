package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.EmployeesService
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Employee
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EmployeesViewModel(
    val service: EmployeesService
) : ViewModel() {
    private val _title = MutableStateFlow(Strings.t("screen.employees.title"))
    val title: StateFlow<String> = _title.asStateFlow()

    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees: StateFlow<List<Employee>> = _employees.asStateFlow()

    fun loadEmployees() {
        viewModelScope.launch {
            _employees.value = service.getEmployees()
        }
    }

    fun clear() {

    }

    fun updateEmployee(updated: Employee) {
        viewModelScope.launch {
            service.updateEmployee(updated)
            _employees.value = service.getEmployees()
        }
    }

    fun deleteEmployee(email: String) {
        viewModelScope.launch {
            service.deleteEmployeeByEmail(email)
            _employees.value = service.getEmployees()
        }
    }

    fun addEmployee(employee: Employee) {
        viewModelScope.launch {
            service.addEmployee(employee)
            _employees.value = service.getEmployees()
        }
    }
}
