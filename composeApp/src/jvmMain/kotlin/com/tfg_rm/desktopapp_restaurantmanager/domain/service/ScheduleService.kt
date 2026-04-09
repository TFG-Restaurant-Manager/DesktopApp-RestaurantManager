package com.tfg_rm.desktopapp_restaurantmanager.domain.service

import com.tfg_rm.desktopapp_restaurantmanager.data.repository.ScheduleRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Employee
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Shift
import java.time.LocalDate

class ScheduleService(
    private val repository: ScheduleRepository
) {
    suspend fun saveEmployeeSchedules(
        employees: List<Employee>,
        weekStart: LocalDate,
        shifts: List<Shift>
    ) = repository.saveEmployeeSchedules(employees, weekStart, shifts)
}
