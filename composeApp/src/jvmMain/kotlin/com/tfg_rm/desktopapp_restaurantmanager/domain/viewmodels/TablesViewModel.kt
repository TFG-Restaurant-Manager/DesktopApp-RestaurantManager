package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Table
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.TablesService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val DEFAULT_TABLE_ID = 1

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
            _tables.value = service.getTables()
        }
    }

    fun addTable(posX: Int, posY: Int, capacity: Int = 4) {
        if (_tables.value.any { it.posX == posX && it.posY == posY }) return
        viewModelScope.launch {
            service.addTable(Table(id = 0, capacity = capacity, posX = posX, posY = posY))
            _tables.value = service.getTables()
        }
    }

    /** Move a non-default table to a new grid cell. Does nothing if the cell is already taken. */
    fun moveTable(id: Int, posX: Int, posY: Int) {
        if (id == DEFAULT_TABLE_ID) return
        if (_tables.value.any { it.posX == posX && it.posY == posY }) return
        viewModelScope.launch {
            val table = _tables.value.firstOrNull { it.id == id } ?: return@launch
            service.updateTable(table.copy(posX = posX, posY = posY))
            _tables.value = service.getTables()
        }
    }

    fun setCapacity(id: Int, capacity: Int) {
        if (capacity < 1) return
        viewModelScope.launch {
            val table = _tables.value.firstOrNull { it.id == id } ?: return@launch
            service.updateTable(table.copy(capacity = capacity))
            _tables.value = service.getTables()
        }
    }

    fun removeTable(id: Int) {
        if (id == DEFAULT_TABLE_ID) return
        viewModelScope.launch {
            service.deleteTable(id)
            _tables.value = service.getTables()
        }
    }
}
