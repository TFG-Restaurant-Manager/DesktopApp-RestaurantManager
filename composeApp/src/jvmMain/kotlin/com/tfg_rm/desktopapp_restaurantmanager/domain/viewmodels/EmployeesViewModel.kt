package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Employee
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.EmployeesService
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.nio.channels.UnresolvedAddressException

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
            try {
                _employees.value = service.getEmployees()
            } catch (e: UnresolvedAddressException) {
                println("Error on loadEmployees in EmployeesViewModel, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on loadEmployees in EmployeesViewModel")
            }
        }
    }

    fun resetCreateState() {
        _createState.value = CreateEmployeeState.Idle
    }

    fun updateEmployee(updated: Employee) {
        viewModelScope.launch {
            try {
                service.updateEmployee(updated)
                _employees.update { list ->
                    list.map { if (it.id == updated.id) updated else it }
                }
            } catch (e: UnresolvedAddressException) {
                println("Error on updateEmployee in EmployeesViewModel, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on updateEmployee in EmployeesViewModel")
            }
        }
    }

    fun updateEmployeePassword(updated: Employee, password: String) {
        viewModelScope.launch {
            try {
                service.updatePassword(updated, password)
            } catch (e: UnresolvedAddressException) {
                println("Error on updateEmployeePassword in EmployeesViewModel, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on updateEmployeePassword in EmployeesViewModel")
            }
        }
    }

    fun deleteEmployee(employee: Employee) {
        viewModelScope.launch {
            try {
                service.deleteEmployee(employee)
                _employees.update { list ->
                    list.filter { it.id != employee.id }
                }
            } catch (e: UnresolvedAddressException) {
                println("Error on deleteEmployee in EmployeesViewModel, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on deleteEmployee in EmployeesViewModel")
            }
        }
    }

    fun addEmployee(employee: Employee, password: String) {
        _createState.value = CreateEmployeeState.Loading
        viewModelScope.launch {
            try {
                service.addEmployee(employee, password)
                _employees.value = service.getEmployees()
                _createState.value = CreateEmployeeState.Success
            } catch (e: UnresolvedAddressException) {
                println("Error on addEmployee in EmployeesViewModel, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                _createState.value = CreateEmployeeState.Error(e.message ?: "Error al crear el empleado")
            }
        }
    }
}
