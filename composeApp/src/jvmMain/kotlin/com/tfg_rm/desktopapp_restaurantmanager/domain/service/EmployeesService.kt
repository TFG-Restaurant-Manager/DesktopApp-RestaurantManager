package com.tfg_rm.desktopapp_restaurantmanager.domain.service

import com.tfg_rm.desktopapp_restaurantmanager.data.repository.EmployeesRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Employee

class EmployeesService(
    private val repository: EmployeesRepository
) {
    suspend fun getEmployees(): List<Employee> = repository.getEmployees()

    fun loadRole(): String? =
        repository.loadRole()

    suspend fun updateEmployee(employee: Employee) = repository.updateEmployee(employee)

    suspend fun updatePassword(employee: Employee, password: String) =
        repository.updatePassword(employee, password)


    suspend fun deleteEmployee(employee: Employee) = repository.deleteEmployee(employee)

    suspend fun addEmployee(employee: Employee, password: String) = repository.addEmployee(employee, password)

    suspend fun saveSchedules(employees: List<Employee>) = repository.saveSchedules(employees)
}
