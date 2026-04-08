package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeScheduleResponse(
    val id: Long,
    val startDatetime: String,
    val endDatetime: String
)

@Serializable
data class EmployeeWithSchedulesResponse(
    val id: Long,
    val name: String,
    val roleName: String,
    val active: Boolean,
    val email: String,
    val phone: String? = null,
    val startDate: String,
    val endDate: String? = null,
    val positionNotes: String? = null,
    val code: String? = null,
    val restaurantName: String,
    val schedules: List<EmployeeScheduleResponse> = emptyList()
)
