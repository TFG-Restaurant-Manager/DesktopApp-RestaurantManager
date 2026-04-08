package com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.EmployeeLoginRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthRemoteDataSource(
    private val client: HttpClient
) {
    suspend fun requestToken(
        code: String,
        password: String
    ): LoginResponse {
        return client.post("api/auth/employeeLogin") {
            contentType(ContentType.Application.Json)
            setBody(EmployeeLoginRequest(code = code, password = password))
        }.body()
    }
}
