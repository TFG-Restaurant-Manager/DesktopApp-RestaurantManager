package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.ScheduleRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.WorkScheduleRequest
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Shift
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter

class ScheduleRepository(
    private val remoteDataSource: ScheduleRemoteDataSource
) {
    private val shifts = mutableListOf<Shift>()
    private val dtFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    suspend fun getShifts(): List<Shift> = shifts.toList()

    suspend fun setShift(shift: Shift) {
        shifts.removeAll { it.employeeRestaurantId == shift.employeeRestaurantId && it.startDateTime.toLocalDate() == shift.startDateTime.toLocalDate() }
        shifts.add(shift)
    }

    suspend fun removeShift(employeeRestaurantId: Int, day: DayOfWeek) {
        shifts.removeAll { it.employeeRestaurantId == employeeRestaurantId && it.startDateTime.dayOfWeek == day }
    }

    suspend fun saveShifts(shiftsToSave: List<Shift>) {
        val requests = shiftsToSave.map { shift ->
            WorkScheduleRequest(
                employeeId = shift.employeeRestaurantId,
                startDatetime = shift.startDateTime.format(dtFormatter),
                endDatetime = shift.endDateTime.format(dtFormatter)
            )
        }
        remoteDataSource.saveSchedules(requests)
    }
}
