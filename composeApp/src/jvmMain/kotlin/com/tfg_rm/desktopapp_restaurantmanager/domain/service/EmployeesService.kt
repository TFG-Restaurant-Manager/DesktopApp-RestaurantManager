package com.tfg_rm.desktopapp_restaurantmanager.domain.service

import com.tfg_rm.desktopapp_restaurantmanager.data.repository.EmployeesRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Employee

class EmployeesService(
    private val repository: EmployeesRepository
) {
    suspend fun getEmployees(): List<Employee> = repository.getEmployees()

    suspend fun updateEmployee(employee: Employee) = repository.updateEmployee(employee)

    suspend fun deleteEmployeeByEmail(email: String) = repository.deleteEmployeeByEmail(email)

    suspend fun addEmployee(employee: Employee) = repository.addEmployee(employee)
}
