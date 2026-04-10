package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeUpdateRequest(
    val name: String,
    val roleName: String,       // MANAGER | WAITER | COOKER | ADMIN
    val active: Boolean = true,
    val email: String,
    val phone: String? = null,
    val startDate: String,      // "YYYY-MM-DD"
    val endDate: String? = null,
    val positionNotes: String? = null,
    val code: String
)
/* "id": 0,
  "name": "string",
  "roleName": "MANAGER",
  "active": true,
  "email": "string",
  "phone": "string",
  "startDate": "2026-04-10",
  "endDate": "2026-04-10",
  "positionNotes": "string",
  "code": "string",
  "password": "string"*/