package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScheduleViewModel {
    private val _title = MutableStateFlow(Strings.t("screen.schedule.title"))
    val title: StateFlow<String> = _title.asStateFlow()
}
