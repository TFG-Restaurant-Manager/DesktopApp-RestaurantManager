package com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.TableCreateRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.TableResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class TablesRemoteDataSource(private val client: HttpClient) {

    suspend fun createTable(request: TableCreateRequest): TableResponse =
        client.post("") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}
