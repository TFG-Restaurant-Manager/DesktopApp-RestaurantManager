package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Shift
import java.time.DayOfWeek

class ScheduleRepository {
    private val shifts = mutableListOf<Shift>()

    suspend fun getShifts(): List<Shift> = shifts.toList()

    suspend fun setShift(shift: Shift) {
        shifts.removeAll { it.employeeEmail == shift.employeeEmail && it.startDateTime.dayOfWeek == shift.startDateTime.dayOfWeek }
        shifts.add(shift)
    }

    suspend fun removeShift(employeeEmail: String, day: DayOfWeek) {
        shifts.removeAll { it.employeeEmail == employeeEmail && it.startDateTime.dayOfWeek == day }
    }
}
