package com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.EmployeeWithSchedulesResponse
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Employee
import java.time.LocalDateTime

fun EmployeeWithSchedulesResponse.toEmployee(): Employee {
    return Employee(
        id = this.id.toInt(),
        roleName = this.roleName,
        name = this.name,
        email = this.email,
        phone = this.phone,
        code = this.code,
        startDate = this.startDate,
        active = this.active,
        schedules = this.schedules.map { objeto ->
            Pair(LocalDateTime.parse(objeto.startDatetime),
                LocalDateTime.parse(objeto.endDatetime))
        }
    )
}