package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Employee
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.EmployeesService
import com.tfg_rm.desktopapp_restaurantmanager.ui.screens.components.UiState
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.delay
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

sealed class SaveScheduleState {
    object Idle : SaveScheduleState()
    object Loading : SaveScheduleState()
    object Success : SaveScheduleState()
    data class Error(val message: String) : SaveScheduleState()
}

class EmployeesViewModel(
    val service: EmployeesService
) : ViewModel() {
    private val _title = MutableStateFlow(Strings.t("screen.employees.title"))
    val title: StateFlow<String> = _title.asStateFlow()

    private val _employees = MutableStateFlow<UiState<List<Employee>>>(UiState.Idle)
    val employees: StateFlow<UiState<List<Employee>>> = _employees.asStateFlow()

    private val _createState = MutableStateFlow<CreateEmployeeState>(CreateEmployeeState.Idle)
    val createState: StateFlow<CreateEmployeeState> = _createState.asStateFlow()

    private val _scheduleState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val scheduleState = _scheduleState.asStateFlow()

    fun loadEmployees() {
        _employees.value = UiState.Loading
        viewModelScope.launch {
            try {
                delay(1000)
                val result = service.getEmployees()
                _employees.value = UiState.Success(result)
            } catch (_: UnresolvedAddressException) {
                println("Error on loadEmployees in EmployeesViewModel, direccion ip no existente")
                _employees.value = UiState.Error(Strings.t("errors.ipadressnotexist"))
            } catch (e: Exception) {
                _employees.value = UiState.Error(Strings.t("errors.undefined"))
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
                _employees.update { state ->
                    if (state is UiState.Success) {
                        UiState.Success(state.data.map { employee ->
                            if (employee.id == updated.id) {
                                updated
                            } else employee
                        })
                    } else state
                }
            } catch (_: UnresolvedAddressException) {
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
            } catch (_: UnresolvedAddressException) {
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
                _employees.update { state ->
                    if (state is UiState.Success) {
                        UiState.Success(state.data.filter { it.id != employee.id })
                    } else state
                }
            } catch (_: UnresolvedAddressException) {
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
                _employees.update { state ->
                    if (state is UiState.Success) {
                        UiState.Success(state.data + employee)
                    } else state
                }
                _createState.value = CreateEmployeeState.Success
            } catch (_: UnresolvedAddressException) {
                println("Error on addEmployee in EmployeesViewModel, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                _createState.value = CreateEmployeeState.Error(e.message ?: "Error al crear el empleado")
            }
        }
    }

    fun saveSchedules() {
        // Misses implementation
    }
}
