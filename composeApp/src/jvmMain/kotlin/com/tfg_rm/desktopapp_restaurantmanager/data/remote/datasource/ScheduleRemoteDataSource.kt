package com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.WorkScheduleRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ScheduleRemoteDataSource(private val client: HttpClient) {

    suspend fun saveSchedules(requests: List<WorkScheduleRequest>) {
        client.post("") {
            contentType(ContentType.Application.Json)
            setBody(requests)
        }
    }
}
