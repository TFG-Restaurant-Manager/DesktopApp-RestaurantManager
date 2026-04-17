package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlin.coroutines.cancellation.CancellationException

class TablesViewModel(
    private val service: TablesService
) : ViewModel() {

    private val _tables = MutableStateFlow<UiState<List<Table>>>(UiState.Idle)
    val tables: StateFlow<UiState<List<Table>>> = _tables.asStateFlow()

    private val _sections = MutableStateFlow<List<String>>(listOf("---"))
    val sections: StateFlow<List<String>> = _sections

    fun resetState() {
        _tables.value = UiState.Idle
        service.clearCache()
    }

    fun addSection(name: String) {
        if (name !in _sections.value) {
            _sections.value += name
        }
    }

    fun loadTables() {
        _tables.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = service.getTables()
                observeSocketMessages()
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

    fun addTable(posX: Int, posY: Int, capacity: Int = 4, seccion: String) {
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
                    status = "AVAILABLE"
                )
                service.addTable(newTable)
                _tables.update { state ->
                    if (state is UiState.Success) {
                        UiState.Success(state.data + newTable)
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
    fun moveTable(id: Int, posX: Int, posY: Int, seccion: String) {
        if ((_tables.value as UiState.Success).data.any { it.posX == posX && it.posY == posY && it.section == seccion }) return
        viewModelScope.launch {
            try {
                val table = (_tables.value as UiState.Success).data.firstOrNull { it.id == id } ?: return@launch
                service.updateTable(table.copy(posX = posX, posY = posY))
                _tables.update { state ->
                    if (state is UiState.Success) {
                        UiState.Success(
                            state.data.map {
                                if (it.id == id) it.copy(posX = posX, posY = posY)
                                else it
                            }
                        )
                    } else state
                }
            } catch (_: UnresolvedAddressException) {
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
                val table = (_tables.value as UiState.Success).data.firstOrNull { it.id == id } ?: return@launch
                service.updateTable(table.copy(capacity = capacity))
                _tables.update { state ->
                    if (state is UiState.Success) {
                        UiState.Success(
                            state.data.map {
                                if (it.id == id) it.copy(capacity = capacity)
                                else it
                            }
                        )
                    } else state
                }
            } catch (_: UnresolvedAddressException) {
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
                _tables.update { state ->
                    if (state is UiState.Success) {
                        UiState.Success(state.data.filter { it.id != id })
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

    private fun observeSocketMessages() {
        viewModelScope.launch {
            try {
                service.observeMessages().collect { message ->
                    println("Mensaje recibido en DishesViewModel: $message")
                }
            } catch (_: CancellationException) {
                service.disconnectWS()
            } catch (e: Exception) {
                e.printStackTrace()
                println(e.message)
            }
        }
    }
}
