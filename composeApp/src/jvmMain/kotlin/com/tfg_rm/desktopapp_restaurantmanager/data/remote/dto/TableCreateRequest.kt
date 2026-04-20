package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TableCreateRequest(
    val tableId: Int?,
    val tableName: String,
    val capacity: Int,
    val posX: Int,
    val posY: Int,
    val sectionId: Int?
)
