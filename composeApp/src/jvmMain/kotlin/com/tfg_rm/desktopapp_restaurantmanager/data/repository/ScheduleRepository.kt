package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Shift
import java.time.DayOfWeek

class ScheduleRepository {
    private val shifts = mutableListOf<Shift>()

    suspend fun getShifts(): List<Shift> = shifts.toList()

    suspend fun setShift(shift: Shift) {
        shifts.removeAll { it.employeeRestaurantId == shift.employeeRestaurantId && it.startDateTime.dayOfWeek == shift.startDateTime.dayOfWeek }
        shifts.add(shift)
    }

    suspend fun removeShift(employeeRestaurantId: Int, day: DayOfWeek) {
        shifts.removeAll { it.employeeRestaurantId == employeeRestaurantId && it.startDateTime.dayOfWeek == day }
    }
}
