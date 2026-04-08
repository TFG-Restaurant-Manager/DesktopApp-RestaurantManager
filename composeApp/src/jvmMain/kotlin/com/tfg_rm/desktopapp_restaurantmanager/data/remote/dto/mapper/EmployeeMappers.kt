package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.mapper

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.EmployeeWithSchedulesResponse
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Employee
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Shift
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

private val dtFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

fun EmployeeWithSchedulesResponse.toDomain(): Employee {
    val shifts = schedules.map { s ->
        val start = parseDateTime(s.startDatetime)
        val end = parseDateTime(s.endDatetime)
        Shift(
            id = s.id.toInt(),
            employeeRestaurantId = this.id.toInt(),
            startDateTime = start,
            endDateTime = end
        )
    }

    return Employee(
        id = id.toInt(),
        roleName = roleName,
        name = name,
        email = email,
        phone = phone ?: "",
        code = code ?: "",
        startDate = startDate,
        active = active,
        shifts = shifts
    )
}

private fun parseDateTime(value: String): LocalDateTime = try {
    LocalDateTime.parse(value, dtFormatter)
} catch (e: Exception) {
    OffsetDateTime.parse(value).toLocalDateTime()
}
