package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.ScheduleRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.WorkScheduleRequest
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Employee
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Shift
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ScheduleRepository(
    private val remoteDataSource: ScheduleRemoteDataSource
) {
    private val dtFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val weekDays = DayOfWeek.values().sortedBy { it.value } // Mon→Sun

    /**
     * For each employee in [employees] (dirty ones), POST their weekly schedule as a
     * JSON array (7 items Mon→Sun). Items are either a WorkScheduleRequest with
     * ISO datetimes, or `null` to indicate a rest day.
     */
    suspend fun saveEmployeeSchedules(
        employees: List<Employee>,
        weekStart: LocalDate,
        shifts: List<Shift>
    ) {
        for (employee in employees) {
            val weekly: List<WorkScheduleRequest?> = weekDays.map { day ->
                val shiftDate = weekStart.plusDays((day.value - 1).toLong())
                val shift = shifts.find {
                    it.employeeRestaurantId == employee.id &&
                        it.startDateTime.toLocalDate() == shiftDate
                }
                if (shift == null) null else WorkScheduleRequest(
                    startDatetime = shift.startDateTime.format(dtFormatter),
                    endDatetime = shift.endDateTime.format(dtFormatter)
                )
            }

            remoteDataSource.saveEmployeeSchedule(
                employeeCode = employee.code,
                weekly = weekly
            )
        }
    }
}
