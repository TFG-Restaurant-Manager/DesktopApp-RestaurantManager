package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.EmployeesService
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.ScheduleService
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Employee
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Shift
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

sealed class SaveScheduleState {
    object Idle : SaveScheduleState()
    object Loading : SaveScheduleState()
    object Success : SaveScheduleState()
    data class Error(val message: String) : SaveScheduleState()
}

class ScheduleViewModel(
    private val scheduleService: ScheduleService,
    private val employeesService: EmployeesService
) : ViewModel() {
    private val _title = MutableStateFlow(Strings.t("screen.schedule.title"))
    val title: StateFlow<String> = _title.asStateFlow()

    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees: StateFlow<List<Employee>> = _employees.asStateFlow()

    private val _shifts = MutableStateFlow<List<Shift>>(emptyList())
    val shifts: StateFlow<List<Shift>> = _shifts.asStateFlow()

    private val _currentWeekStart = MutableStateFlow(LocalDate.now().with(DayOfWeek.MONDAY))
    val currentWeekStart: StateFlow<LocalDate> = _currentWeekStart.asStateFlow()

    private val _saveState = MutableStateFlow<SaveScheduleState>(SaveScheduleState.Idle)
    val saveState: StateFlow<SaveScheduleState> = _saveState.asStateFlow()

    fun loadSchedule() {
        viewModelScope.launch {
            val emps = employeesService.getEmployees()
            _employees.value = emps
            _shifts.value = emps.flatMap { it.shifts }
        }
    }

    fun setShift(shift: Shift) {
        viewModelScope.launch {
            scheduleService.setShift(shift)
            _shifts.value = scheduleService.getShifts()
        }
    }

    fun removeShift(employeeRestaurantId: Int, day: DayOfWeek) {
        viewModelScope.launch {
            scheduleService.removeShift(employeeRestaurantId, day)
            _shifts.value = scheduleService.getShifts()
        }
    }

    fun nextWeek() {
        _currentWeekStart.value = _currentWeekStart.value.plusWeeks(1)
    }

    fun prevWeek() {
        _currentWeekStart.value = _currentWeekStart.value.minusWeeks(1)
    }

    fun saveSchedule() {
        viewModelScope.launch {
            _saveState.value = SaveScheduleState.Loading
            runCatching {
                val weekStart = _currentWeekStart.value
                val weekEnd = weekStart.plusDays(6)
                val weekShifts = _shifts.value.filter {
                    val date = it.startDateTime.toLocalDate()
                    !date.isBefore(weekStart) && !date.isAfter(weekEnd)
                }
                scheduleService.saveShifts(weekShifts)
            }.onSuccess {
                _saveState.value = SaveScheduleState.Success
            }.onFailure {
                // Present a friendlier, less noisy error message to the UI.
                _saveState.value = SaveScheduleState.Error(
                    "Error al guardar horarios. Comprueba la conexión y la URL del servidor."
                )
            }
        }
    }

    fun resetSaveState() {
        _saveState.value = SaveScheduleState.Idle
    }

    fun clear() {}
}
