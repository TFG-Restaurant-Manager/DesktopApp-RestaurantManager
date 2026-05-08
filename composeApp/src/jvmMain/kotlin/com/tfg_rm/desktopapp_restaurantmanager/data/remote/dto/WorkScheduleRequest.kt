package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class WorkScheduleRequest(
    val employeeId: Int,
    val startDatetime: String,
    val endDatetime: String
)
