package com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.EmployeeRegisterRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.EmployeeUpdateRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.EmployeeWithSchedulesResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class EmployeesRemoteDataSource(
    private val client: HttpClient
) {

    suspend fun getEmployees(): List<EmployeeWithSchedulesResponse> = client.get("api/employee").body()

    suspend fun registerEmployee(request: EmployeeRegisterRequest) {
        client.post("api/employee") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun deleteEmployee(employeeId: Int) {
        val deleteSuccesfull = client.delete("api/employee/$employeeId").body<Boolean>()
        if (!deleteSuccesfull) throw Exception("Error on delete succesfull")
    }

    suspend fun updateEmployee(employeeId: Int, employee: EmployeeUpdateRequest) {
        client.put("api/employee/$employeeId") {
            contentType(ContentType.Application.Json)
            setBody(employee)
        }
    }
}
