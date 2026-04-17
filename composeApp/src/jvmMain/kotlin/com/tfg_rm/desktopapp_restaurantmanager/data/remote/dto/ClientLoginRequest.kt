package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ClientLoginRequest(
    val email: String,
    val password: String,
    val restaurantId: Long
)
