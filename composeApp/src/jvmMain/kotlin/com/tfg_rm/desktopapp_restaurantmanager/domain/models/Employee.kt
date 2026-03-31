package com.tfg_rm.desktopapp_restaurantmanager.domain.models

data class Employee(
    // ── Server fields (DB: employees + employee_restaurants join) ────────
    val id: Int,
    val roleName: String,
    val name: String,
    val email: String,
    val phone: String,
    val active: Boolean = true
    // schedules come from work_schedules table → use Shift model separately
)
