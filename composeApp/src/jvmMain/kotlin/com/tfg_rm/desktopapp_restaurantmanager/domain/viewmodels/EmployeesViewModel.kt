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

sealed class CreateEmployeeState {
    object Idle : CreateEmployeeState()
    object Loading : CreateEmployeeState()
    object Success : CreateEmployeeState()
    data class Error(val msg: String) : CreateEmployeeState()
}

class EmployeesViewModel(
    val service: EmployeesService
) : ViewModel() {
    private val _title = MutableStateFlow(Strings.t("screen.employees.title"))
    val title: StateFlow<String> = _title.asStateFlow()

    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees: StateFlow<List<Employee>> = _employees.asStateFlow()

    private val _createState = MutableStateFlow<CreateEmployeeState>(CreateEmployeeState.Idle)
    val createState: StateFlow<CreateEmployeeState> = _createState.asStateFlow()

    fun loadEmployees() {
        viewModelScope.launch {
            _employees.value = service.getEmployees()
        }
    }

    fun resetCreateState() {
        _createState.value = CreateEmployeeState.Idle
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

    fun addEmployee(employee: Employee, password: String) {
        _createState.value = CreateEmployeeState.Loading
        viewModelScope.launch {
            try {
                service.addEmployee(employee, password)
                _employees.value = service.getEmployees()
                _createState.value = CreateEmployeeState.Success
            } catch (e: Exception) {
                e.printStackTrace()
                _createState.value = CreateEmployeeState.Error(e.message ?: "Error al crear el empleado")
            }
        }
    }
}
