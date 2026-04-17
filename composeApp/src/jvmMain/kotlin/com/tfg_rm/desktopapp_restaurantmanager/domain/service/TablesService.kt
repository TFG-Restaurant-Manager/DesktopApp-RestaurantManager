package com.tfg_rm.desktopapp_restaurantmanager.domain.service

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
            Table(
                id = it.tableId,
                capacity = it.capacity,
                section = it.sectionTitle,
                posX = it.posX.toInt(),
                posY = it.posY.toInt(),
                status = it.status
            )
        }

    suspend fun addTable(table: Table) = repository.addTable(table)
    suspend fun updateTable(table: Table) = repository.updateTable(table)
    suspend fun deleteTable(id: Int) = repository.deleteTable(id)

    fun clearCache() = repositoryDuo.clearCache()

    fun observeMessages() = repository.observeMessages()

    suspend fun sendMessage(message: String) = repository.sendMessage(message)

    suspend fun disconnectWS() = repository.disconnectWS()
}
