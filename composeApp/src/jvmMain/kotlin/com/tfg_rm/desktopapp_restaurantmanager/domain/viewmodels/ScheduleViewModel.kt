package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.ScheduleService
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScheduleViewModel(
    val service: ScheduleService
) : ViewModel() {
    private val _title = MutableStateFlow(Strings.t("screen.schedule.title"))
    val title: StateFlow<String> = _title.asStateFlow()

    fun loadSchedule() {
        viewModelScope.launch {
            service.loadInitialData()
        }
    }

    fun clear() {

    }
}
