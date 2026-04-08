package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.TablesRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.TableCreateRequest
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Table

class TablesRepository(
    private val remoteDataSource: TablesRemoteDataSource
) {

    private var nextId = 7

    /** Table 1 is fixed at (col=1, row=1) and cannot be moved. */

    suspend fun getTables(): List<Table> = listOf()

    suspend fun addTable(table: Table): Table {
        val request = TableCreateRequest(
            tableName    = table.name.ifBlank { "Mesa $nextId" },
            capacity     = table.capacity,
            posX         = table.posX,
            posY         = table.posY,
            restaurantId = table.restaurantId
        )
        val response = remoteDataSource.createTable(request)
        val withServerId = table.copy(
            id   = response.tableId.toInt(),
            name = response.tableName
        )
        return withServerId
    }

    suspend fun updateTable(updated: Table) {
        // In implementation
    }

    suspend fun deleteTable(id: Int) {
        // In implementation
    }
}
