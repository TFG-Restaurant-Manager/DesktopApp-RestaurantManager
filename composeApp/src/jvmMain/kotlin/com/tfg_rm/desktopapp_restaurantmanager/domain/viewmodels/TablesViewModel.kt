package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.TablesService
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TablesViewModel(
    val service: TablesService
) : ViewModel() {
    private val _title = MutableStateFlow(Strings.t("screen.tables.title"))
    val title: StateFlow<String> = _title.asStateFlow()

    fun loadTables() {
        viewModelScope.launch {
            service.loadInitialData()
        }
    }

    fun clear() {

    }
}
