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

    fun loadSchedule() {
        viewModelScope.launch {
            _employees.value = employeesService.getEmployees()
            _shifts.value = scheduleService.getShifts()
        }
    }

    fun setShift(shift: Shift) {
        viewModelScope.launch {
            scheduleService.setShift(shift)
            _shifts.value = scheduleService.getShifts()
        }
    }

    fun removeShift(employeeEmail: String, day: DayOfWeek) {
        viewModelScope.launch {
            scheduleService.removeShift(employeeEmail, day)
            _shifts.value = scheduleService.getShifts()
        }
    }

    fun clear() {}
}
