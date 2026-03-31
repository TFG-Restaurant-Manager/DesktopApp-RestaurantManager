package com.tfg_rm.desktopapp_restaurantmanager.domain.service

import com.tfg_rm.desktopapp_restaurantmanager.data.repository.TablesRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Table

class TablesService(
    private val repository: TablesRepository
) {
    suspend fun getTables(): List<Table>      = repository.getTables()
    suspend fun addTable(table: Table): Table = repository.addTable(table)
    suspend fun updateTable(table: Table)     = repository.updateTable(table)
    suspend fun deleteTable(id: Int)          = repository.deleteTable(id)
}
