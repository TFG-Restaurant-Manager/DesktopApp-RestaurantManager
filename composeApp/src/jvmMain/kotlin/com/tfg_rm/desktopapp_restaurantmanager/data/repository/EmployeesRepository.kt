package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.EmployeesRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.EmployeeRegisterRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.toDomain
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Employee

class EmployeesRepository(
    private val remote: EmployeesRemoteDataSource
) {
    private val employees = mutableListOf<Employee>()

    suspend fun getEmployees(): List<Employee> = employees.toList()

    suspend fun updateEmployee(updated: Employee) {
        val idx = employees.indexOfFirst { it.id == updated.id }
        if (idx >= 0) employees[idx] = updated else employees.add(updated)
    }

    suspend fun deleteEmployeeByEmail(email: String) {
        employees.removeAll { it.email == email }
    }

    suspend fun addEmployee(employee: Employee, password: String): Employee {
        val request = EmployeeRegisterRequest(
            name = employee.name,
            roleName = employee.roleName,
            email = employee.email,
            phone = employee.phone.ifEmpty { null },
            startDate = employee.startDate,
            code = employee.code,
            password = password
        )
        val response = remote.registerEmployee(request)
        val created = response.toDomain()
        employees.add(created)
        return created
    }
}
