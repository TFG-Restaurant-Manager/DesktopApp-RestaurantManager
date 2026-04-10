package com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.EmployeeRegisterRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.EmployeeUpdateRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.EmployeeWithSchedulesResponse
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Employee
import java.time.LocalDate
import java.time.LocalDateTime

fun EmployeeWithSchedulesResponse.toEmployee(): Employee {
    return Employee(
        id = this.id.toInt(),
        roleName = this.roleName,
        name = this.name,
        email = this.email,
        phone = this.phone,
        code = this.code,
        startDate = LocalDate.parse(this.startDate),
        endDate = LocalDate.parse(this.endDate ?: LocalDate.now().toString()),
        active = this.active,
        positionNotes = this.positionNotes,
        schedules = this.schedules.map { objeto ->
            Pair(
                LocalDateTime.parse(objeto.startDatetime),
                LocalDateTime.parse(objeto.endDatetime)
            )
        }
    )
}

fun Employee.toEmployeeRegisterRequest(password: String): EmployeeRegisterRequest {
    return EmployeeRegisterRequest(
        name = this.name,
        roleName = this.roleName,
        active = this.active,
        email = this.email,
        phone = this.phone,
        startDate = this.startDate.toString(),
        endDate = this.endDate.toString(),
        positionNotes = this.positionNotes,
        code = this.code,
        password = password
    )
}

fun Employee.toEmployeeUpdateRequest(): EmployeeUpdateRequest {
    return EmployeeUpdateRequest(
        name = this.name,
        roleName = this.roleName,
        active = this.active,
        email = this.email,
        phone = this.phone,
        startDate = this.startDate.toString(),
        endDate = this.endDate.toString(),
        positionNotes = this.positionNotes,
        code = this.code
    )
}