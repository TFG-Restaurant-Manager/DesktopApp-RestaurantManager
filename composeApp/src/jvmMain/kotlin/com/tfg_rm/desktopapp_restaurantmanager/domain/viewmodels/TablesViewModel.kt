package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.network.SessionManager
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Section
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Table
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.TablesService
import com.tfg_rm.desktopapp_restaurantmanager.ui.screens.components.UiState
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.nio.channels.UnresolvedAddressException

class TablesViewModel(
    private val service: TablesService
) : ViewModel() {

    private val _tables = MutableStateFlow<UiState<List<Table>>>(UiState.Idle)
    val tables: StateFlow<UiState<List<Table>>> = _tables.asStateFlow()

    private val _sections = MutableStateFlow(listOf(Section(null, "---")))
    val sections: StateFlow<List<Section>> = _sections

    init {
        viewModelScope.launch {
            SessionManager.sessionExpired.collect {
                resetState()
            }
        }
    }

    fun resetState() {
        _tables.value = UiState.Idle
        service.clearCache()
    }

    fun addSection(name: String): Section {
        var section = _sections.value.firstOrNull { it.name == name }
        if (section == null) {
            section = Section(null, name)
            _sections.update {
                it + section
            }
        }
        return section
    }

    fun loadTables() {
        _tables.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = service.getTables()
                _tables.value = UiState.Success(result)
                _sections.value = (_tables.value as UiState.Success).data.map { it.section }.distinct()
            } catch (_: UnresolvedAddressException) {
                _tables.value = UiState.Error(Strings.t("errors.ipadressnotexist"))
                println("Error on loadTables in tables view model, direccion ip no existente")
            } catch (e: Exception) {
                _tables.value = UiState.Error(Strings.t("errors.undefined"))
                e.printStackTrace()
                println("Error on loadTables in tables view model")
            }
        }
    }

    fun addTable(posX: Int, posY: Int, capacity: Int = 4, seccion: Section) {
        if ((_tables.value as UiState.Success).data.any { it.posX == posX && it.posY == posY }) return
        viewModelScope.launch {
            try {
                val list = (_tables.value as UiState.Success).data.map { it.id }
                val nextId = (1..list.size + 1).first { it !in list.toSet() }
                val newTable = Table(
                    id = nextId,
                    capacity = capacity,
                    posX = posX,
                    posY = posY,
                    section = seccion,
                    status = "AVAILABLE",

                    )
                val tables = (_tables.value as UiState.Success).data + newTable
                _tables.update { state ->
                    if (state is UiState.Success) {
                        UiState.Success(tables)
                    } else state
                }
            } catch (_: UnresolvedAddressException) {
                println("Error on addTable in tables view model, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on addTable in tables view model")
            }
        }
    }

    /** Move a non-default table to a new grid cell. Does nothing if the cell is already taken. */
    fun moveTable(table: Table, posX: Int, posY: Int, seccion: Section) {
        if ((_tables.value as UiState.Success).data.any { it.posX == posX && it.posY == posY && it.section == seccion }) return
        viewModelScope.launch {
            try {
                if ((_tables.value as UiState.Success).data.firstOrNull { it == table } != null) {
                    _tables.update { state ->
                        if (state is UiState.Success) {
                            UiState.Success(
                                state.data.map {
                                    if (it == table) it.copy(posX = posX, posY = posY)
                                    else it
                                }
                            )
                        } else state
                    }
                }
            } catch (_: UnresolvedAddressException) {
                println("Error on moveTable in tables view model, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on moveTable in tables view model")
            }
        }
    }

    fun setCapacity(table: Table, capacity: Int) {
        if (capacity < 1) return
        viewModelScope.launch {
            try {
                if ((_tables.value as UiState.Success).data.firstOrNull { it == table } != null) {
                    _tables.update { state ->
                        if (state is UiState.Success) {
                            UiState.Success(
                                state.data.map {
                                    if (it == table) it.copy(capacity = capacity)
                                    else it
                                }
                            )
                        } else state
                    }
                }
            } catch (_: UnresolvedAddressException) {
                println("Error on setCapacity in tables view model, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on setCapacity in tables view model")
            }
        }
    }

    fun removeTable(table: Table) {
        viewModelScope.launch {
            try {
                _tables.update { state ->
                    if (state is UiState.Success) {
                        UiState.Success(state.data.filter { it != table })
                    } else state
                }
            } catch (_: UnresolvedAddressException) {
                println("Error on removeTable in tables view model, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on removeTable in tables view model")
            }
        }
    }

    fun saveData() {
        viewModelScope.launch {
            try {
                val state = _tables.value
                if (state is UiState.Success) {
                    val tables = state.data
                    service.updateTable(tables)
                    service.clearCache()
                    val result = service.getTables()
                    result.forEach { println(it.toString()) }
                    _tables.value = UiState.Success(result)
                    _sections.value = (_tables.value as UiState.Success).data.map { it.section }.distinct()
                }
            } catch (_: UnresolvedAddressException) {
                println("Error on saveData in tables view model, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on saveData in tables view model")
            }
        }
    }
}
