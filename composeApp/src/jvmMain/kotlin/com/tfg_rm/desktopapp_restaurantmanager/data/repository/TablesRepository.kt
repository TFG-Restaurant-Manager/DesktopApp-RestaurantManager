package com.tfg_rm.desktopapp_restaurantmanager.data.repository

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.datasource.TablesRemoteDataSource
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper.toTableCreateRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.network.SocketManager
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.network.TokenProvider
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Table

class TablesRepository(
    private val remoteDataSource: TablesRemoteDataSource,
    private val tokenProvider: TokenProvider,
    private val socketManager: SocketManager
) {

    /** Table 1 is fixed at (col=1, row=1) and cannot be moved. */

    suspend fun getTables(): List<Table> = listOf()

    suspend fun updateTables(tables: List<Table>) {
        remoteDataSource.updateTables(tables.map { it.toTableCreateRequest() })
    }

    fun loadRole(): String? =
        tokenProvider.getRole()

    fun observeMessages() = socketManager.messages

    suspend fun sendMessage(message: String) = socketManager.sendMessage(message)

    suspend fun disconnectWS() = socketManager.disconnect()
}
