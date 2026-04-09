package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Table
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.TablesService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.nio.channels.UnresolvedAddressException

class TablesViewModel(
    private val service: TablesService
) : ViewModel() {

    private val _tables = MutableStateFlow<List<Table>>(emptyList())
    val tables: StateFlow<List<Table>> = _tables.asStateFlow()

    init {
        loadTables()
    }

    private fun loadTables() {
        viewModelScope.launch {
            try {
                _tables.value = service.getTables()
            } catch (e: UnresolvedAddressException) {
                println("Error on loadTables in tables view model, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on loadTables in tables view model")
            }
        }
    }

    fun addTable(posX: Int, posY: Int, capacity: Int = 4) {
        if (_tables.value.any { it.posX == posX && it.posY == posY }) return
        viewModelScope.launch {
            try {
                val list = _tables.value.map { it.id }
                val nextId = (1..list.size + 1).first { it !in list.toSet() }
                val newTable = Table(id = nextId, capacity = capacity, posX = posX, posY = posY, status = "AVAILABLE")
                service.addTable(newTable)
                _tables.update { actualList ->
                    actualList + newTable
                }
            } catch (e: UnresolvedAddressException) {
                println("Error on addTable in tables view model, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on addTable in tables view model")
            }
        }
    }

    /** Move a non-default table to a new grid cell. Does nothing if the cell is already taken. */
    fun moveTable(id: Int, posX: Int, posY: Int) {
        if (_tables.value.any { it.posX == posX && it.posY == posY }) return
        viewModelScope.launch {
            try {
                val table = _tables.value.firstOrNull { it.id == id } ?: return@launch
                service.updateTable(table.copy(posX = posX, posY = posY))
                val tableUpdate = _tables.value.find { it.id == id }
                tableUpdate?.posY = posY
                tableUpdate?.posX = posX
            } catch (e: UnresolvedAddressException) {
                println("Error on moveTable in tables view model, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on moveTable in tables view model")
            }
        }
    }

    fun setCapacity(id: Int, capacity: Int) {
        if (capacity < 1) return
        viewModelScope.launch {
            try {
                val table = _tables.value.firstOrNull { it.id == id } ?: return@launch
                service.updateTable(table.copy(capacity = capacity))
                _tables.value = service.getTables()
            } catch (e: UnresolvedAddressException) {
                println("Error on setCapacity in tables view model, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on setCapacity in tables view model")
            }
        }
    }

    fun removeTable(id: Int) {
        viewModelScope.launch {
            try {
                service.deleteTable(id)
                val tableEliminate = _tables.value.find { it.id == id }
                _tables.update { tablaActual ->
                    tablaActual.minus(tableEliminate!!)
                }
            } catch (e: UnresolvedAddressException) {
                println("Error on removeTable in tables view model, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on removeTable in tables view model")
            }
        }
    }
}
