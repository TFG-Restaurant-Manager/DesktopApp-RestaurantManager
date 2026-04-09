package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import kotlinx.serialization.Serializable

/** One day entry in the employee's weekly schedule. null fields mean the employee rests. */
@Serializable
data class DayScheduleEntry(
    val date: String,        // "YYYY-MM-DD"
    val startTime: String?,  // "HH:mm", or null for a rest day
    val endTime: String?     // "HH:mm", or null for a rest day
)

@Serializable
data class EmployeeWeekScheduleRequest(
    val weekStart: String,           // "YYYY-MM-DD" – Monday of the week
    val days: List<DayScheduleEntry> // 7 entries, Monday → Sunday
)
