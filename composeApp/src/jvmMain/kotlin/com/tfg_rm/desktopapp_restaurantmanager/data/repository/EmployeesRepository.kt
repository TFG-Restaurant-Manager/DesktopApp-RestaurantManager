package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.EmployeesRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.EmployeeRegisterRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper.toEmployee
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Employee

class EmployeesRepository(
    private val remote: EmployeesRemoteDataSource
) {

    suspend fun getEmployees(): List<Employee> = remote.getEmployees().map { it.toEmployee() }

    suspend fun updateEmployee(updated: Employee) {
        // Falta implementacion remote data source
    }

    suspend fun deleteEmployeeByEmail(email: String) {
        // Falta implementacion remote data source
    }

    suspend fun addEmployee(employee: Employee, password: String) {
        // Falta implementacion remote data source
    }
}
