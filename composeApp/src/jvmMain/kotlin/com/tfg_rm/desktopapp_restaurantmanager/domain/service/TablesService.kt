package com.tfg_rm.desktopapp_restaurantmanager.domain.service

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper.toTable
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.TablesOrdersRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.TablesRepository
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Table

class TablesService(
    private val repository: TablesRepository,
    private val repositoryDuo: TablesOrdersRepository
) {
    suspend fun getTables(): List<Table> = repositoryDuo.getTablesAndOrders()
        .distinctBy { it.tableId }
        .map {
            it.toTable()
        }

    suspend fun updateTable(tables: List<Table>) = repository.updateTables(tables)

    fun clearCache() = repositoryDuo.clearCache()

    fun observeMessages() = repository.observeMessages()

    suspend fun disconnectWS() = repository.disconnectWS()
}
