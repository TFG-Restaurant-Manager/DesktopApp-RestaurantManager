package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Employee
import java.time.LocalDateTime

class EmployeesRepository {
    private val now = LocalDateTime.now()

    // In-memory list used for the fake repository
    private val employees = mutableListOf(
        Employee(
            roleName = "Cocinero",
            name = "Ana López",
            email = "ana.lopez@example.com",
            phone = "+34 600 111 222",
            schedules = listOf(Pair(now.minusHours(8), now.plusHours(4)))
        ),
        Employee(
            roleName = "Camarero",
            name = "Carlos Martín",
            email = "carlos.martin@example.com",
            phone = "+34 611 222 333",
            schedules = listOf(Pair(now.minusHours(2), now.plusHours(6)))
        ),
        Employee(
            roleName = "Gerente",
            name = "Laura Ruiz",
            email = "laura.ruiz@example.com",
            phone = "+34 622 333 444",
            schedules = listOf(Pair(now.minusDays(1).withHour(9), now.minusDays(1).withHour(17)))
        )
    )

    suspend fun getEmployees(): List<Employee> = employees.toList()

    suspend fun updateEmployee(updated: Employee) {
        val idx = employees.indexOfFirst { it.email == updated.email }
        if (idx >= 0) {
            employees[idx] = updated
        } else {
            employees.add(updated)
        }
    }

    suspend fun deleteEmployeeByEmail(email: String) {
        employees.removeAll { it.email == email }
    }

    suspend fun addEmployee(employee: Employee) {
        employees.add(employee)
    }
}
