package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.TablesRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.network.SocketManager
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Table

class TablesRepository(
    private val remoteDataSource: TablesRemoteDataSource,
    private val socketManager: SocketManager
) {

    private var nextId = 7

    /** Table 1 is fixed at (col=1, row=1) and cannot be moved. */

    suspend fun getTables(): List<Table> = listOf()

    suspend fun addTable(tables: List<Table>) {
        //remoteDataSource.updateTables(tables.map { it.toTableCreateRequest() })
    }

    suspend fun updateTable(tables: List<Table>) {
        //remoteDataSource.updateTables(tables.map { it.toTableCreateRequest() })
    }

    suspend fun deleteTable(tables: List<Table>) {
        //remoteDataSource.updateTables(tables.map { it.toTableCreateRequest() })
    }

    fun observeMessages() = socketManager.messages

    suspend fun sendMessage(message: String) = socketManager.sendMessage(message)

    suspend fun disconnectWS() = socketManager.disconnect()
}
