package com.tfg_rm.desktopapp_restaurantmanager.domain.models

import java.time.LocalDateTime

data class Shift(
    // ── Server fields (DB: work_schedules) ──────────────────────────────
    val id: Int = 0,
    val employeeRestaurantId: Int,  // FK to employee_restaurants.id
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime
)
