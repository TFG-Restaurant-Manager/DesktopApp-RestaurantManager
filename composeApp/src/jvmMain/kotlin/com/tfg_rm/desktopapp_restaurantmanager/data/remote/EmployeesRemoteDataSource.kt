package com.tfg_rm.desktopapp_restaurantmanager.data.remote

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.EmployeeRegisterRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.EmployeeWithSchedulesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class EmployeesRemoteDataSource(
    private val client: HttpClient
) {
    suspend fun registerEmployee(request: EmployeeRegisterRequest): EmployeeWithSchedulesResponse {
        return client.post("employee/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
