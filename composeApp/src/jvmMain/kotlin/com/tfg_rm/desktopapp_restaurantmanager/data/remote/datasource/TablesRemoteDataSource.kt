package com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.TableCreateRequest
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class TablesRemoteDataSource(private val client: HttpClient) {

    suspend fun updateTables(request: List<TableCreateRequest>) {
        client.put("api/table") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
}
