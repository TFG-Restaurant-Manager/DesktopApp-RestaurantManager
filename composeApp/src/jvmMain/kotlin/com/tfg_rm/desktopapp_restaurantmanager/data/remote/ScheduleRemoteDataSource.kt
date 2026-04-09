package com.tfg_rm.desktopapp_restaurantmanager.data.remote

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.WorkScheduleRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ScheduleRemoteDataSource(private val client: HttpClient) {

    /**
     * Saves the weekly schedule for one employee via the per-employee endpoint.
     * The body is a JSON array (7 items Mon→Sun) where items are either an object
     * with `startDatetime`/`endDatetime` ISO strings, or `null` to indicate rest.
     */
    suspend fun saveEmployeeSchedule(
        employeeCode: String,
        weekly: List<WorkScheduleRequest?>
    ) {
        client.post("api/work-schedules/employee/$employeeCode") {
            contentType(ContentType.Application.Json)
            setBody(weekly)
        }
    }
}
