package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.TablesRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.TableCreateRequest
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Table

class TablesRepository(
    private val remoteDataSource: TablesRemoteDataSource
) {

    private var nextId = 7

    /** Table 1 is fixed at (col=1, row=1) and cannot be moved. */
    private val tables = mutableListOf(
        Table(id = 1, name = "Mesa 1", capacity = 4, posX = 1, posY = 1),
        Table(id = 2, name = "Mesa 2", capacity = 2, posX = 2, posY = 1),
        Table(id = 3, name = "Mesa 3", capacity = 4, posX = 3, posY = 1),
        Table(id = 4, name = "Mesa 4", capacity = 6, posX = 1, posY = 2),
        Table(id = 5, name = "Mesa 5", capacity = 4, posX = 2, posY = 2),
        Table(id = 6, name = "Mesa 6", capacity = 8, posX = 3, posY = 2),
    )

    suspend fun getTables(): List<Table> = tables.toList()

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
        tables.add(withServerId)
        return withServerId
    }

    suspend fun updateTable(updated: Table) {
        val idx = tables.indexOfFirst { it.id == updated.id }
        if (idx >= 0) tables[idx] = updated
    }

    suspend fun deleteTable(id: Int) {
        tables.removeAll { it.id == id }
    }
}
