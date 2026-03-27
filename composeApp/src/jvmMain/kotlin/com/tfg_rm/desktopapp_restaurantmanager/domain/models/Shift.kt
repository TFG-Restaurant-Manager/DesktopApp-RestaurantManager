package com.tfg_rm.desktopapp_restaurantmanager.domain.models

import java.time.LocalDateTime

data class Shift(
    val employeeEmail: String,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime
)
