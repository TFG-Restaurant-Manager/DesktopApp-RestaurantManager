package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleSend(
    val employeeId: Long,
    val scheduleId: Long,
    val startTime: String,
    val endTime: String
)