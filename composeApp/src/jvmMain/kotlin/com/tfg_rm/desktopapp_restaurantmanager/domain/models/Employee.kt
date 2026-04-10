package com.tfg_rm.desktopapp_restaurantmanager.domain.models

import java.time.LocalDate
import java.time.LocalDateTime

data class Employee(
    val id: Int,
    val roleName: String,
    val name: String,
    val email: String,
    val phone: String?,
    val code: String = "",
    val startDate: LocalDate = LocalDate.now().minusDays(3),
    val endDate: LocalDate = LocalDate.now().plusDays(5),
    val positionNotes: String?,
    val active: Boolean = true,
    val schedules: List<Pair<LocalDateTime, LocalDateTime>>
)
