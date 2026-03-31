package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Employee

class EmployeesRepository {
    private var nextId = 4

    private val employees = mutableListOf(
        Employee(id = 1, roleName = "Cocinero",  name = "Ana López",     email = "ana.lopez@example.com",     phone = "+34 600 111 222", active = true),
        Employee(id = 2, roleName = "Camarero",  name = "Carlos Martín", email = "carlos.martin@example.com", phone = "+34 611 222 333", active = true),
        Employee(id = 3, roleName = "Gerente",   name = "Laura Ruiz",    email = "laura.ruiz@example.com",    phone = "+34 622 333 444", active = true)
    )

    suspend fun getEmployees(): List<Employee> = employees.toList()

    suspend fun updateEmployee(updated: Employee) {
        val idx = employees.indexOfFirst { it.id == updated.id }
        if (idx >= 0) employees[idx] = updated else employees.add(updated)
    }

    suspend fun deleteEmployeeByEmail(email: String) {
        employees.removeAll { it.email == email }
    }

    suspend fun addEmployee(employee: Employee): Employee {
        val withId = employee.copy(id = nextId++)
        employees.add(withId)
        return withId
    }
}
