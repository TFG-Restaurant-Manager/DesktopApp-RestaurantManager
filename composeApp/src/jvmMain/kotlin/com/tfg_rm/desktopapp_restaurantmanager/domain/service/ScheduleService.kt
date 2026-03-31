package com.tfg_rm.desktopapp_restaurantmanager.domain.service

import com.tfg_rm.desktopapp_restaurantmanager.data.repository.ScheduleRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Shift
import java.time.DayOfWeek

class ScheduleService(
    private val repository: ScheduleRepository
) {
    suspend fun getShifts(): List<Shift> = repository.getShifts()

    suspend fun setShift(shift: Shift) = repository.setShift(shift)

    suspend fun removeShift(employeeRestaurantId: Int, day: DayOfWeek) =
        repository.removeShift(employeeRestaurantId, day)
}
