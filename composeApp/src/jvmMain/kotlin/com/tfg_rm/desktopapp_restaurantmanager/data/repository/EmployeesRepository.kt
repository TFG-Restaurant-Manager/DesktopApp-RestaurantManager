package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.EmployeesRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper.toEmployee
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper.toEmployeeRegisterRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper.toEmployeeUpdateRequest
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Employee

class EmployeesRepository(
    private val remote: EmployeesRemoteDataSource
) {

    suspend fun getEmployees(): List<Employee> = remote.getEmployees().map { it.toEmployee() }

    suspend fun updateEmployee(updated: Employee) =
        remote.updateEmployee(updated.id, updated.toEmployeeUpdateRequest())

    suspend fun updatePassword(employee: Employee, password: String) =
        remote.updatePassword(employee.id, password)

    suspend fun deleteEmployee(employee: Employee) =
        remote.deleteEmployee(employee.id)

    suspend fun addEmployee(employee: Employee, password: String) =
        remote.registerEmployee(employee.toEmployeeRegisterRequest(password))
}
